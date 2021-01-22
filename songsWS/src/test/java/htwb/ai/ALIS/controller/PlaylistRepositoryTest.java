package htwb.ai.ALIS.controller;

import htwb.ai.ALIS.model.PlaylistBuilder;
import htwb.ai.ALIS.repository.PlaylistRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
/**
 * Simple repo test to test if the hsqldb in memory database is working correctly.
 */
public class PlaylistRepositoryTest {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Test
    public void whenFindingPlaylistByOwnerId_thenCorrect() {
        playlistRepository.save(new PlaylistBuilder().setIsPrivate(true).setName("Somename").createPlaylist());
        assertThat(playlistRepository.findById(1)).isInstanceOf(Optional.class);
    }

    @Test
    public void whenFindingAllPlaylists_thenCorrect() {
        playlistRepository.save(new PlaylistBuilder().setIsPrivate(true).setName("Somename").createPlaylist());
        assertThat(playlistRepository.findAll()).isInstanceOf(List.class);
    }

}
