package htwb.ai.ALIS.service;

import htwb.ai.ALIS.model.Playlist;
import htwb.ai.ALIS.model.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PlaylistService {

    /**
     * Get all playlists from a user.
     * Private user playlists only are returned if you are the user.
     *
     * @param owner owner
     * @return list of playlists
     */
    List<Playlist> getPlaylistsFromUser(User owner);

    /**
     * Get a playlist by it's ID
     *
     * @param id id
     * @return playlist
     */
    Optional<Playlist> getPlayListById(int id);

    /**
     * Save a playlist.
     *
     * @param playlist playlist
     */
    int savePlaylist(Playlist playlist);

    /**
     * Delete a playlist.
     *
     * @param id id
     * @return true if worked, else false
     */
    boolean removePlaylist(int id);

    boolean putPlaylist(int id);
}
