package htwb.ai.ALIS.controller;

import htwb.ai.ALIS.model.Song;
import htwb.ai.ALIS.service.SongService;
import htwb.ai.ALIS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class SongController {

    @Autowired
    SongService songService;

    @Autowired
    UserService userService;

    public SongController(SongService songService, UserService userService) {
        this.userService = userService;
        this.songService = songService;
    }


    private boolean noValidToken(String token) {
        return ! userService.validateToken(token);
    }

    @GetMapping(value = "/songs/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getSong(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable int id) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<Song> loadedSong = songService.findSong(id);
            if (loadedSong.isPresent()) {
                return ResponseEntity.ok(loadedSong.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }


    @GetMapping(value = "/songs", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getAllSongs(@RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            return ResponseEntity.ok(songService.findAllSongs());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/songs", consumes = {"application/json"})
    public ResponseEntity<?> postSong(@RequestHeader(value = "Authorization", required = false) String token, @Valid @RequestBody Song song) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            //save user
            int newSongID = songService.saveSong(song);
            String newURL = "/rest/songs/" + newSongID;
            URI uri = new URI(newURL);
            //set location header to newly created user
            return ResponseEntity.created(uri).build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/songs/{id}", consumes = {"application/json"})
    public ResponseEntity<?> putSong(@RequestHeader(value = "Authorization", required = false) String token, @Valid @RequestBody Song song, @PathVariable int id) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            if (song.getId() == id) {
                boolean songFound = songService.overwriteSong(song, id);
                if (songFound) {
                    return ResponseEntity.noContent().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping(value = "/songs/{id}")
    public ResponseEntity<?> deleteSong(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable int id) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            boolean songFound = songService.deleteSong(id);
            if (songFound) {
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}
