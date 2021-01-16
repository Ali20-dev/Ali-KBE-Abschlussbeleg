package htwb.ai.ALIS.repository;

import htwb.ai.ALIS.model.Song;
import javassist.NotFoundException;

import javax.persistence.PersistenceException;
import java.util.List;

public interface DBSongDAO {
    /**
     * Saves a Song to the database.
     *
     * @param song song
     * @return int ID of the song
     * @throws PersistenceException if something goes wrong with the connection
     */
    int saveSong(Song song) throws PersistenceException;

    /**
     * Finds all Songs in the database and returns them in a list. The list
     * can be empty.
     *
     * @return List of songs
     * @throws PersistenceException if something goes wrong with the connection
     */
    List<Song> findAllSongs() throws PersistenceException;

    /**
     * Finds the song matching the given songId in the database.
     *
     * @param songId int ID of the song
     * @return Song
     * @throws NotFoundException    If the song couldn't be found
     * @throws PersistenceException if something goes wrong with the connection
     */
    Song findSong(int songId) throws NotFoundException, PersistenceException;

    /**
     * Overwrites an existing song in the database.
     *
     * @param song   replacement song
     * @param songId int ID of the song which is to be replaced
     * @throws NotFoundException    If the song couldn't be found
     * @throws PersistenceException if something goes wrong with the connection
     */
    void overwriteSong(Song song, int songId) throws NotFoundException, PersistenceException;

    /**
     * Deletes an existing song from the database.
     *
     * @param songId int ID of the song
     * @throws NotFoundException    If the song couldn't be found
     * @throws PersistenceException if something goes wrong with the connection
     */
    void deleteSong(int songId) throws NotFoundException, PersistenceException;
}
