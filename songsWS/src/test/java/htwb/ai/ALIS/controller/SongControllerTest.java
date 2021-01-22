package htwb.ai.ALIS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.ALIS.controller.SongController;
import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.model.SongBuilder;
import htwb.ai.ALIS.service.SongService;
import htwb.ai.ALIS.service.UserService;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SongController.class)
public class SongControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SongService mockedSongService;

    @MockBean
    private UserService mockedUserService;

    private List<Song> listOfSongs;
    private Song validSong, invalidSong;

    @BeforeEach
    public void setUp() {
        Song song1 = new SongBuilder().setTitle("Country Roads").setArtist("Elton John")
                .setReleased(1973).setLabel("Sony").createSong();
        Song song2 = new SongBuilder().setTitle("Mamamia").createSong();
        invalidSong = new SongBuilder().setArtist("Nope").createSong();
        validSong = new SongBuilder().setTitle("Validboy").createSong();
        listOfSongs = new ArrayList<>();
        listOfSongs.add(song1);
        listOfSongs.add(song2);
        mockedSongService = Mockito.mock(SongService.class);
        mockedUserService = Mockito.mock(UserService.class);
        Mockito.when(mockedSongService.findAllSongs()).thenReturn(listOfSongs);
        Mockito.when(mockedSongService.saveSong(Mockito.any(Song.class))).thenReturn(2);
        Mockito.when(mockedSongService.findSong(1)).thenReturn(java.util.Optional.ofNullable(song1));
        Mockito.when(mockedSongService.findSong(2)).thenReturn(java.util.Optional.ofNullable(song2));
        Mockito.when(mockedSongService.findSong(5)).thenReturn(Optional.empty());
        Mockito.when(mockedSongService.overwriteSong(Mockito.any(), Mockito.eq(1))).thenReturn(true);
        Mockito.when(mockedSongService.overwriteSong(Mockito.any(), Mockito.eq(5))).thenReturn(false);
        Mockito.when(mockedSongService.deleteSong(Mockito.eq(1))).thenReturn(true);
        Mockito.when(mockedSongService.deleteSong(Mockito.eq(5))).thenReturn(false);
        Mockito.when(mockedUserService.validateToken(Mockito.any())).thenReturn(true);
        Mockito.when(mockedUserService.validateToken("invalid")).thenReturn(false);
        mockMvc = MockMvcBuilders.standaloneSetup(new SongController(mockedSongService, mockedUserService)).build();
    }

    @Test
    void testPostingValidSong() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSong = objectMapper.writeValueAsString(validSong);
        mockMvc.perform(post("/rest/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "blub")
                .content(jsonSong))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/rest/songs/2"));
        Mockito.verify(mockedSongService, Mockito.times(1)).saveSong(Mockito.any());
    }

    @Test
    void testPostingInvalidSong() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSong = objectMapper.writeValueAsString(invalidSong);
        mockMvc.perform(post("/rest/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "blub")
                .content(jsonSong))
                .andExpect(status().is4xxClientError());
        Mockito.verify(mockedSongService, Mockito.never()).saveSong(Mockito.any());
    }

    @Test
    void testGettingAllSongsJSON() throws Exception {
        mockMvc.perform(get("/rest/songs").accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "blub"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize(2)))
                .andExpect(jsonPath("$[0].title", CoreMatchers.is(listOfSongs.get(0).getTitle())))
                .andExpect(jsonPath("$[1].title", CoreMatchers.is(listOfSongs.get(1).getTitle())));
        Mockito.verify(mockedSongService, Mockito.times(1)).findAllSongs();
    }


    @Test
    void testGettingAllSongsXML() throws Exception {
        mockMvc.perform(get("/rest/songs")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "blub"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));

        Mockito.verify(mockedSongService, Mockito.times(1)).findAllSongs();
    }

    @Test
    void testGettingAnExistingSongJSON() throws Exception {
        mockMvc.perform(get("/rest/songs/1")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "blub"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", CoreMatchers.is(listOfSongs.get(0).getTitle())));
        Mockito.verify(mockedSongService, Mockito.times(1)).findSong(1);
    }

    @Test
    void testGettingAnExistingSongXML() throws Exception {
        mockMvc.perform(get("/rest/songs/1")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "blub"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
        Mockito.verify(mockedSongService, Mockito.times(1)).findSong(1);
    }

    @Test
    void testGettingANonExistingSongJSON() throws Exception {
        mockMvc.perform(get("/rest/songs/5")
                .accept(MediaType.APPLICATION_JSON)
                .header("Authorization", "blub"))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedSongService, Mockito.times(1)).findSong(5);
    }

    @Test
    void testGettingANonExistingSongXML() throws Exception {
        mockMvc.perform(get("/rest/songs/5")
                .accept(MediaType.APPLICATION_XML)
                .header("Authorization", "blub"))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedSongService, Mockito.times(1)).findSong(5);
    }

    @Test
    void testPuttingAValidSongThatExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(1).setTitle("updated").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/rest/songs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong)
                .header("Authorization", "blub"))
                .andExpect(status().isNoContent());
        Mockito.verify(mockedSongService, Mockito.times(1)).overwriteSong(Mockito.any(Song.class), Mockito.eq(1));
    }

    @Test
    void testPuttingAValidSongThatDoesNotExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(5).setTitle("updated").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/rest/songs/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong)
                .header("Authorization", "blub"))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedSongService, Mockito.times(1)).overwriteSong(Mockito.any(Song.class), Mockito.eq(5));
    }

    @Test
    void testPuttingAnInvalidSong() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(1).createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/rest/songs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong)
                .header("Authorization", "blub"))
                .andExpect(status().isBadRequest());
        Mockito.verify(mockedSongService, Mockito.never()).overwriteSong(Mockito.any(Song.class), Mockito.eq(1));
    }

    @Test
    void testPuttingWithWrongIdInObjectAndPath() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(2).setTitle("Bla").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/rest/songs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong)
                .header("Authorization", "blub"))
                .andExpect(status().isBadRequest());
        Mockito.verify(mockedSongService, Mockito.never()).overwriteSong(Mockito.any(Song.class), Mockito.eq(1));
    }

    @Test
    void testDeletingExistingSong() throws Exception {
        mockMvc.perform(delete("/rest/songs/1")
                .header("Authorization", "blub"))
                .andExpect(status().isNoContent());
        Mockito.verify(mockedSongService, Mockito.times(1)).deleteSong(1);
    }

    @Test
    void testDeletingNonExistingSong() throws Exception {
        mockMvc.perform(delete("/rest/songs/5")
                .header("Authorization", "blub"))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedSongService, Mockito.times(1)).deleteSong(5);
    }

    @Test
    @DirtiesContext
    void getSongByIdWithoutToken_andStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/rest/songs/1")
                .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void getAllSongsWithoutToken_andStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/rest/songs")
                .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void postSongWithoutToken_andStatusIsUnauthorized() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(1).setTitle("updated").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(post("/rest/songs")
                .header("Authorization", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void putSongWithoutToken_andStatusIsUnauthorized() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(1).setTitle("updated").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/rest/songs/1")
                .header("Authorization", "invalid")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void deleteSongWithoutToken_andStatusIsUnauthorized() throws Exception {
        mockMvc.perform(delete("/rest/songs/1")
                .header("Authorization", "invalid"))
                .andExpect(status().isUnauthorized());
    }

}