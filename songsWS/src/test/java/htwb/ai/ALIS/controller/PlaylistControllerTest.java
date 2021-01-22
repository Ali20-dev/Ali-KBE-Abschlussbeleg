package htwb.ai.ALIS.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import htwb.ai.ALIS.model.*;
import htwb.ai.ALIS.service.PlaylistService;
import htwb.ai.ALIS.service.SongService;
import htwb.ai.ALIS.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PlaylistControllerTest {

    MockMvc mockMvc;

    @Qualifier("songServiceImpl")
    @Autowired
    SongService songService;

    @Qualifier("userServiceImpl")
    @Autowired
    UserService userService;

    @Autowired
    PlaylistService playlistService;

    String eschulerToken, mmusterToken;

    Playlist eschulerPlaylist, eschulerPrivatePlaylist, playlistWithNotExistingSongs;

    /**
     * Setup the real repositories for integration testing.
     */
    @BeforeEach
    public void setUp() {
        Song song1 = new SongBuilder().setTitle("Country Roads").setArtist("Elton John")
                .setReleased(1973).setLabel("Sony").createSong();
        Song song2 = new SongBuilder().setTitle("Mamamia").createSong();
        User eschuler = new UserBuilder().setUserId("eschuler").setPassword("pass1234").
                setFirstName("Blub").setLastName("Bla").createUser();
        User mmuster = new UserBuilder().setUserId("mmuster").setPassword("pass1234").
                setFirstName("Blub").setLastName("Bla").createUser();
        userService.registerUser(eschuler);
        userService.registerUser(mmuster);
        songService.saveSong(song1);
        songService.saveSong(song2);
        eschulerToken = "tokenblabla";
        mmusterToken = "tokenOfMmuster";
        userService.saveToken(eschuler, eschulerToken);
        userService.saveToken(mmuster, mmusterToken);
        Playlist playlist = new PlaylistBuilder().setSongList(songService.findAllSongs()).setName("eschuler_private")
                .setIsPrivate(false).setOwnerId(eschuler).createPlaylist();
        Playlist privatePlaylist = new PlaylistBuilder().setSongList(songService.findAllSongs()).setName("eschuler_private")
                .setIsPrivate(true).setOwnerId(eschuler).createPlaylist();
        List<Song> nonExistingSongs = new ArrayList<Song>();
        nonExistingSongs.add(new SongBuilder().setId(9999).setTitle("blub").createSong());
        playlistWithNotExistingSongs = new PlaylistBuilder()
                .setSongList(nonExistingSongs)
                .setIsPrivate(true).createPlaylist();
        eschulerPlaylist = playlist;
        eschulerPrivatePlaylist = privatePlaylist;
        playlistService.savePlaylist(playlist);
        playlistService.savePlaylist(privatePlaylist);
        PlaylistController playlistController = new PlaylistController(playlistService, userService, songService);
        mockMvc = MockMvcBuilders.standaloneSetup(playlistController).build();
    }

    @Test
    @DirtiesContext
    void getPlaylistOfEschuelerAsEschueler_andResultIsCorrect() throws Exception {
        MvcResult result = mockMvc.perform(get("/rest/songLists?userId=eschuler")
                .header("Authorization", eschulerToken))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(result.getResponse().getContentAsString());
        try {
            JsonNode jsonNode = objectMapper.readValue(result.getResponse().getContentAsString(), JsonNode.class);
            List<Playlist> resultPlaylists = objectMapper.convertValue(jsonNode, new TypeReference<List<Playlist>>() {
            });
            assertEquals(2, resultPlaylists.size());
            assertEquals("eschuler", resultPlaylists.get(0).getOwnerId().getUserId());
            assertEquals(eschulerPlaylist.getSongList().get(0).getTitle(), resultPlaylists.get(0).getSongList().get(0).getTitle());
            assertEquals(eschulerPlaylist.getSongList().get(1).getTitle(), resultPlaylists.get(0).getSongList().get(1).getTitle());
            assertEquals(eschulerPrivatePlaylist.getSongList().get(0).getTitle(), resultPlaylists.get(1).getSongList().get(0).getTitle());
            assertEquals(eschulerPrivatePlaylist.getSongList().get(1).getTitle(), resultPlaylists.get(1).getSongList().get(1).getTitle());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Something went wrong!");
        }
    }

    @Test
    @DirtiesContext
    void getPlaylistOfEschuelerAsMmuster_andResultIsCorrect() throws Exception {
        MvcResult result = mockMvc.perform(get("/rest/songLists?userId=eschuler")
                .header("Authorization", mmusterToken))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(result.getResponse().getContentAsString());
        try {
            JsonNode jsonNode = objectMapper.readValue(result.getResponse().getContentAsString(), JsonNode.class);
            List<Playlist> resultPlaylists = objectMapper.convertValue(jsonNode, new TypeReference<List<Playlist>>() {
            });
            assertEquals(1, resultPlaylists.size());
            assertEquals("eschuler", resultPlaylists.get(0).getOwnerId().getUserId());
            assertEquals(eschulerPlaylist.getSongList().get(0).getTitle(), resultPlaylists.get(0).getSongList().get(0).getTitle());
            assertEquals(eschulerPlaylist.getSongList().get(1).getTitle(), resultPlaylists.get(0).getSongList().get(1).getTitle());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Something went wrong!");
        }
    }

    @Test
    @DirtiesContext
    void getPrivatePlaylistByExistingIdAuthenticated_andResultIsCorrect() throws Exception {
        MvcResult result = mockMvc.perform(get("/rest/songLists/2")
                .header("Authorization", eschulerToken))
                .andExpect(status().isOk()).andReturn();
        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(result.getResponse().getContentAsString());
        try {
            JsonNode jsonNode = objectMapper.readValue(result.getResponse().getContentAsString(), JsonNode.class);
            Playlist resultPlaylists = objectMapper.convertValue(jsonNode, new TypeReference<Playlist>() {
            });
            assertEquals("eschuler", resultPlaylists.getOwnerId().getUserId());
            assertEquals(eschulerPrivatePlaylist.getSongList().get(0).getTitle(), resultPlaylists.getSongList().get(0).getTitle());
            assertEquals(eschulerPrivatePlaylist.getSongList().get(1).getTitle(), resultPlaylists.getSongList().get(1).getTitle());
        } catch (IOException e) {
            e.printStackTrace();
            fail("Something went wrong!");
        }
    }

    @Test
    @DirtiesContext
    void postPlaylistWithExistingSongs_andResultIsCreated() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPlaylist = objectMapper.writeValueAsString(eschulerPlaylist);
        mockMvc.perform(post("/rest/songLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPlaylist)
                .header("Authorization", mmusterToken))
                .andExpect(status().isCreated());
    }

    @Test
    @DirtiesContext
    void postPlaylistWithNonExistingSongs_andResultIsBadRequest() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPlaylist = objectMapper.writeValueAsString(playlistWithNotExistingSongs);
        mockMvc.perform(post("/rest/songLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPlaylist)
                .header("Authorization", mmusterToken))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DirtiesContext
    void deletePlaylistAsAuthorizedUser_andStatusIsNoContent() throws Exception {
        mockMvc.perform(delete("/rest/songLists/1")
                .header("Authorization", eschulerToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DirtiesContext
    void deletePlaylistAsNonAuthorizedUser_andStatusIsForbidden() throws Exception {
        mockMvc.perform(delete("/rest/songLists/1")
                .header("Authorization", mmusterToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    void deleteNonExistingPlaylist_andStatusIsNotFound() throws Exception {
        mockMvc.perform(delete("/rest/songLists/9999")
                .header("Authorization", eschulerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void getPrivatePlaylistByExistingIdNotAuthenticated_andResultIsForbidden() throws Exception {
        mockMvc.perform(get("/rest/songLists/2")
                .header("Authorization", mmusterToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DirtiesContext
    void getNonExistingPlaylistById_andResultIsNotFound() throws Exception {
        mockMvc.perform(get("/rest/songLists/999")
                .header("Authorization", mmusterToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void getPlaylistOfMmuster_andStatusIsNotFound() throws Exception {
        mockMvc.perform(get("/rest/songLists?userId=mmuster")
                .header("Authorization", mmusterToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void getPlaylistOfNonExistingUser_andStatusIsNotFound() throws Exception {
        mockMvc.perform(get("/rest/songLists?userId=notExisting")
                .header("Authorization", eschulerToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DirtiesContext
    void getPlaylistOfValidUserWithoutToken_andStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/rest/songLists?userId=eschuler")
                .header("Authorization", "don't know"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void getPlaylistFromIdWithoutToken_andStatusIsUnauthorized() throws Exception {
        mockMvc.perform(get("/rest/songLists/1")
                .header("Authorization", "don't know"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void postPlaylistWithoutToken_andStatusIsUnauthorized() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonPlaylist = objectMapper.writeValueAsString(eschulerPlaylist);
        mockMvc.perform(post("/rest/songLists")
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPlaylist)
                .header("Authorization", "don't know"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DirtiesContext
    void deletePlaylistWithoutToken_andStatusIsUnauthorized() throws Exception {
        mockMvc.perform(delete("/rest/songLists/1")
                .header("Authorization", "don't know"))
                .andExpect(status().isUnauthorized());
    }

}