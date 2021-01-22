package htwb.ai.ALIS.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "playlists")
@JacksonXmlRootElement
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int playlistId;

    @ManyToOne(cascade = CascadeType.MERGE)
    @Fetch(FetchMode.JOIN)
    @JoinColumn(name = "ownerId", referencedColumnName = "userId")
    // we don't need these properties when getting owner
    @JsonIgnoreProperties({"firstName", "lastName", "password", "accessToken", "hibernateLazyInitializer", "handler"})
    private User ownerId;

    @ManyToMany(cascade = CascadeType.MERGE)
    @JoinTable(name = "playlist_songs",
            joinColumns = {@JoinColumn(name = "playlist_id", referencedColumnName = "playlistId")},
            inverseJoinColumns = {@JoinColumn(name = "song_id", referencedColumnName = "id")})
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @Fetch(FetchMode.JOIN)
    private List<Song> songList;

    @Column
    private String name;

    @Column
    private boolean isPrivate;

    public Playlist() {
    }

    public Playlist(int playlistId, User ownerId, List<Song> songList, String name, boolean isPrivate) {
        this.playlistId = playlistId;
        this.ownerId = ownerId;
        this.songList = songList;
        this.name = name;
        this.isPrivate = isPrivate;
    }

    public int getPlaylistId() {
        return playlistId;
    }

    public void setPlaylistId(int id) {
        this.playlistId = id;
    }

    public User getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(User ownerId) {
        this.ownerId = ownerId;
    }

    public List<Song> getSongList() {
        return songList;
    }

    public void setSongList(List<Song> songList) {
        this.songList = songList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
}
