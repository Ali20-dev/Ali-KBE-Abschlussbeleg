package htwb.ai.ALIS.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.model.SongBuilder;
import htwb.ai.ALIS.repository.DBSongDAO;
import javassist.NotFoundException;
import org.hamcrest.CoreMatchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest
class SongControllerTest {

    private MockMvc mockMvc;
    private DBSongDAO mockedDBSongDAO;
    private List<Song> listOfSongs;
    private Song validSong, invalidSong;

    @BeforeEach
    public void setUp() throws Exception {
        Song song1 = new SongBuilder().setTitle("Country Roads").setArtist("Elton John").setReleased(1973).setLabel("Sony").createSong();
        Song song2 = new SongBuilder().setTitle("Mamamia").createSong();
        invalidSong = new SongBuilder().setArtist("Nope").createSong();
        validSong = new SongBuilder().setTitle("Validboy").createSong();
        listOfSongs = new ArrayList<>();
        listOfSongs.add(song1);
        listOfSongs.add(song2);
        mockedDBSongDAO = Mockito.mock(DBSongDAO.class);
        Mockito.when(mockedDBSongDAO.findAllSongs()).thenReturn(listOfSongs);
        Mockito.when(mockedDBSongDAO.saveSong(Mockito.any(Song.class))).thenReturn(2);
        Mockito.when(mockedDBSongDAO.findSong(1)).thenReturn(song1);
        Mockito.when(mockedDBSongDAO.findSong(2)).thenReturn(song2);
        Mockito.when(mockedDBSongDAO.findSong(5)).thenThrow(NotFoundException.class);
        Mockito.doThrow(NotFoundException.class).when(mockedDBSongDAO).overwriteSong(Mockito.any(), Mockito.eq(5));
        Mockito.doThrow(NotFoundException.class).when(mockedDBSongDAO).deleteSong(Mockito.eq(5));
        mockMvc = MockMvcBuilders.standaloneSetup(new SongController(mockedDBSongDAO)).build();
    }

    @Test
    void testPostingValidSong() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSong = objectMapper.writeValueAsString(validSong);
        mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isCreated())
                .andExpect(header().string("location", "/rest/songs/2"));
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).saveSong(Mockito.any());
    }

    @Test
    void testPostingInvalidSong() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonSong = objectMapper.writeValueAsString(invalidSong);
        mockMvc.perform(post("/songs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().is4xxClientError());
        Mockito.verify(mockedDBSongDAO, Mockito.never()).saveSong(Mockito.any());
    }

    @Test
    void testGettingAllSongsJSON() throws Exception {
        mockMvc.perform(get("/songs").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", IsCollectionWithSize.hasSize(2)))
                .andExpect(jsonPath("$[0].title", CoreMatchers.is(listOfSongs.get(0).getTitle())))
                .andExpect(jsonPath("$[1].title", CoreMatchers.is(listOfSongs.get(1).getTitle())));
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).findAllSongs();
    }


    @Test
    void testGettingAllSongsXML() throws Exception {
        mockMvc.perform(get("/songs")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));

        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).findAllSongs();
    }

    @Test
    void testGettingAnExistingSongJSON() throws Exception {
        mockMvc.perform(get("/songs/1")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title", CoreMatchers.is(listOfSongs.get(0).getTitle())));
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).findSong(1);
    }

    @Test
    void testGettingAnExistingSongXML() throws Exception {
        mockMvc.perform(get("/songs/1")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_XML));
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).findSong(1);
    }

    @Test
    void testGettingANonExistingSongJSON() throws Exception {
        mockMvc.perform(get("/songs/5")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).findSong(5);
    }

    @Test
    void testGettingANonExistingSongXML() throws Exception {
        mockMvc.perform(get("/songs/5")
                .accept(MediaType.APPLICATION_XML))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).findSong(5);
    }

    @Test
    void testPuttingAValidSongThatExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(1).setTitle("updated").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/songs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isNoContent());
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).overwriteSong(Mockito.any(Song.class), Mockito.eq(1));
    }

    @Test
    void testPuttingAValidSongThatDoesNotExists() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(5).setTitle("updated").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/songs/5")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).overwriteSong(Mockito.any(Song.class), Mockito.eq(5));
    }

    @Test
    void testPuttingAnInvalidSong() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(1).createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/songs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isBadRequest());
        Mockito.verify(mockedDBSongDAO, Mockito.never()).overwriteSong(Mockito.any(Song.class), Mockito.eq(1));
    }

    @Test
    void testPuttingWithWrongIdInObjectAndPath() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Song song = new SongBuilder().setId(2).setTitle("Bla").createSong();
        String jsonSong = objectMapper.writeValueAsString(song);
        mockMvc.perform(put("/songs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonSong))
                .andExpect(status().isBadRequest());
        Mockito.verify(mockedDBSongDAO, Mockito.never()).overwriteSong(Mockito.any(Song.class), Mockito.eq(1));
    }

    @Test
    void testDeletingExistingSong() throws Exception {
        mockMvc.perform(delete("/songs/1"))
                .andExpect(status().isNoContent());
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).deleteSong(1);
    }

    @Test
    void testDeletingNonExistingSong() throws Exception {
        mockMvc.perform(delete("/songs/5"))
                .andExpect(status().isNotFound());
        Mockito.verify(mockedDBSongDAO, Mockito.times(1)).deleteSong(5);
    }

}