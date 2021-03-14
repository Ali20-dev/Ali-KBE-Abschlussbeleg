package htwb.ai.ALIS.service;

import htwb.ai.ALIS.model.Playlist;
import htwb.ai.ALIS.model.User;
import htwb.ai.ALIS.repository.PlaylistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlaylistServiceImpl implements PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    public PlaylistServiceImpl() {

    }

    public PlaylistServiceImpl(PlaylistRepository playlistRepository) {
        this.playlistRepository = playlistRepository;
    }

    @Override
    public List<Playlist> getPlaylistsFromUser(User owner) {
        return playlistRepository.findAllByOwnerId(owner);
    }

    @Override
    public Optional<Playlist> getPlayListById(int id) {
        return playlistRepository.findById(id);
    }

    @Override
    public int savePlaylist(Playlist playlist) {
        return playlistRepository.save(playlist).getPlaylistId();
    }

    @Override
    public boolean removePlaylist(int id) {
        Optional<Playlist> loadedPlaylist = playlistRepository.findById(id);
        if (loadedPlaylist.isPresent()) {
            playlistRepository.delete(loadedPlaylist.get());
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean putPlaylist(int id) {
        return false;
    }
}
