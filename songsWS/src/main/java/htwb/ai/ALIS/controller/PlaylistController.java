package htwb.ai.ALIS.controller;

import htwb.ai.ALIS.model.Playlist;
import htwb.ai.ALIS.model.User;
import htwb.ai.ALIS.service.PlaylistService;
import htwb.ai.ALIS.service.SongService;
import htwb.ai.ALIS.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/rest")
public class PlaylistController {

    @Autowired
    PlaylistService playlistService;

    @Autowired
    UserService userService;

    @Autowired
    SongService songService;

    public PlaylistController(PlaylistService playlistService, UserService userService, SongService songService) {
        this.userService = userService;
        this.playlistService = playlistService;
        this.songService = songService;
    }

    private boolean noValidToken(String token) {
        return !userService.validateToken(token);
    }

    @GetMapping(value = "/songLists", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getPlaylists(@RequestHeader(value = "Authorization", required = false) String token, @RequestParam String userId) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> accessingUser = userService.getUserForToken(token);
            Optional<User> owner = userService.getUserById(userId);
            if (owner.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            List<Playlist> playlistsOfOwner = playlistService.getPlaylistsFromUser(owner.get());
            if (playlistsOfOwner.size() != 0 && accessingUser.isPresent()) {
                if (!owner.get().getUserId().equals(accessingUser.get().getUserId())) {
                    // remove playlists that are private
                    playlistsOfOwner.removeIf(Playlist::isPrivate);
                }
                return ResponseEntity.ok(playlistsOfOwner);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping(value = "/songLists/{id}", produces = {"application/json", "application/xml"})
    public ResponseEntity<?> getPlaylist(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable int id) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> accessingUser = userService.getUserForToken(token);
            Optional<Playlist> loadedPlaylist = playlistService.getPlayListById(id);
            if (loadedPlaylist.isPresent() && accessingUser.isPresent()) {
                User owner = loadedPlaylist.get().getOwnerId();
                if (owner.getUserId().equals(accessingUser.get().getUserId()) || !loadedPlaylist.get().isPrivate()) {
                    return ResponseEntity.ok(loadedPlaylist.get());
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
                }
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping(value = "/songLists", consumes = {"application/json"})
    public ResponseEntity<?> postPlaylist(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody @Valid Playlist playlist) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            System.out.println(playlist.isPrivate());
            Optional<User> accessingUser = userService.getUserForToken(token);
            boolean valid = playlist.getSongList().stream().anyMatch(song ->
                    songService.exists(song.getId()));
            if (valid && accessingUser.isPresent()) {
                playlist.setOwnerId(accessingUser.get());
                int newPlaylistId = playlistService.savePlaylist(playlist);
                String newURL = "/rest/songs/" + newPlaylistId;
                URI uri = new URI(newURL);
                return ResponseEntity.created(uri).build();
            } else {
                return ResponseEntity.badRequest().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/songLists/{id}")
    public ResponseEntity<?> deletePlaylist(@RequestHeader(value = "Authorization", required = false) String token, @PathVariable int id) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> accessingUser = userService.getUserForToken(token);
            if (accessingUser.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Optional<Playlist> loadedPlaylist = playlistService.getPlayListById(id);
            if (loadedPlaylist.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else if (accessingUser.get().getUserId().equals(loadedPlaylist.get().getOwnerId().getUserId())) {
                playlistService.removePlaylist(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping(value = "/songsLists", consumes = {"application/json"})
    public ResponseEntity<?> putPlaylist(@RequestHeader(value = "Authorization", required = false) String token, @RequestBody @Valid Playlist playlist, @PathVariable int id) {
        try {
            if (noValidToken(token)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
            Optional<User> accessingUser = userService.getUserForToken(token);
            if (accessingUser.isEmpty()) {
                return ResponseEntity.badRequest().build();
            }
            Optional<Playlist> loadedPlaylist = playlistService.getPlayListById(id);
            if (loadedPlaylist.isEmpty()) {
                return ResponseEntity.notFound().build();
            } else if (accessingUser.get().getUserId().equals(loadedPlaylist.get().getOwnerId().getUserId())) {
                playlistService.removePlaylist(id);
                return ResponseEntity.noContent().build();
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }


    }
}
