package htwb.ai.ALIS.service;

import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SongServiceImpl implements SongService {

    @Autowired
    private SongRepository songRepository;

    public SongServiceImpl(){

    }

    public SongServiceImpl(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public int saveSong(Song song) {
        return songRepository.saveAndFlush(song).getId();
    }

    @Override
    public List<Song> findAllSongs() {
        return songRepository.findAll();
    }

    @Override
    public Optional<Song> findSong(int songId) {
        return songRepository.findById(songId);
    }

    @Override
    public boolean overwriteSong(Song song, int songId) {
        Optional<Song> loadedSong = songRepository.findById(songId);
        if (loadedSong.isPresent()) {
            if (loadedSong.get().getId().equals(song.getId())) {
                loadedSong.get().setArtist(song.getArtist());
                loadedSong.get().setLabel(song.getLabel());
                loadedSong.get().setReleased(song.getReleased());
                loadedSong.get().setTitle(song.getTitle());
                songRepository.saveAndFlush(loadedSong.get());
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public boolean deleteSong(int songId) {
        Optional<Song> loadedSong = songRepository.findById(songId);
        if (loadedSong.isPresent()) {
            songRepository.deleteById(songId);
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean exists(int songId) {
        return songRepository.existsById(songId);
    }
}
