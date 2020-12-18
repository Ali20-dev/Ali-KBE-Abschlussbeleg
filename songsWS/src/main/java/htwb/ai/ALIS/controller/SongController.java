package htwb.ai.ALIS.controller;

import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.repository.DBSongDAO;
import javassist.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping("/songs")
public class SongController {

    private DBSongDAO dbSongDAO;

    public SongController(DBSongDAO dbSongDAO) {
        this.dbSongDAO = dbSongDAO;
    }

    public SongController() {

    }

    @GetMapping(value = "/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getSong(@PathVariable int id) {
        try {
            return ResponseEntity.ok(dbSongDAO.findSong(id));
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getAllSongs() {
        try {
            //return dbSongDAO.findAllSongs();
            return ResponseEntity.ok(dbSongDAO.findAllSongs());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(consumes = {"application/json"})
    public ResponseEntity<?> postSong(@Valid @RequestBody Song song) {
        try {
            //save user
            int newSongID = dbSongDAO.saveSong(song);
            String newURL = "/rest/songs/" + newSongID;
            URI uri = new URI(newURL);
            //set location header to newly created user
            return ResponseEntity.created(uri).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/{id}", consumes = {"application/json"})
    public ResponseEntity<?> putSong(@Valid @RequestBody Song song, @PathVariable int id) {
        System.out.println(id + ", " + song.getId());
        try {
            if (song.getId() == id) {
                dbSongDAO.overwriteSong(song, id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> deleteSong(@PathVariable int id) {
        try {
            dbSongDAO.deleteSong(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
