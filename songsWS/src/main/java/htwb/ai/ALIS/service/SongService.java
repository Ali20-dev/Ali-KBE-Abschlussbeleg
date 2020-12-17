package htwb.ai.ALIS.service;

import htwb.ai.ALIS.model.Song;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SongService {
    /**
     * Saves a Song to the database.
     *
     * @param song song
     * @return int ID of the song
     */
    int saveSong(Song song);

    /**
     * Finds all Songs in the database and returns them in a list. The list
     * can be empty.
     *
     * @return List of songs
     */
    List<Song> findAllSongs();

    /**
     * Finds the song matching the given songId in the database.
     *
     * @param songId int ID of the song
     * @return Song
     */
    Optional<Song> findSong(int songId);

    /**
     * Overwrites an existing song in the database.
     *
     * @param song   replacement song
     * @param songId int ID of the song which is to be replaced
     */
    boolean overwriteSong(Song song, int songId);

    /**
     * Deletes an existing song from the database.
     *
     * @param songId int ID of the song
     */
    boolean deleteSong(int songId);

    /**
     * Returns if song exists or not
     * @param songId id of song
     * @return true, if song exists, else false
     */
    boolean exists(int songId);
}
