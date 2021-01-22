package htwb.ai.ALIS.model;

import java.util.List;

public class PlaylistBuilder {
    private int id;
    private User ownerId;
    private List<Song> songList;
    private String name;
    private boolean isPrivate;

    public PlaylistBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public PlaylistBuilder setOwnerId(User ownerId) {
        this.ownerId = ownerId;
        return this;
    }

    public PlaylistBuilder setSongList(List<Song> songList) {
        this.songList = songList;
        return this;
    }

    public PlaylistBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public PlaylistBuilder setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
        return this;
    }

    public Playlist createPlaylist() {
        return new Playlist(id, ownerId, songList, name, isPrivate);
    }
}