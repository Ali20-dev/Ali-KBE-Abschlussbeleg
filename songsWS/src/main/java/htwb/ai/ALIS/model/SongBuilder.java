package htwb.ai.ALIS.model;

public class SongBuilder {
    private String title;
    private String artist;
    private String label;
    private int released;
    private int id;

    public SongBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public SongBuilder setArtist(String artist) {
        this.artist = artist;
        return this;
    }

    public SongBuilder setLabel(String label) {
        this.label = label;
        return this;
    }

    public SongBuilder setReleased(int released) {
        this.released = released;
        return this;
    }

    public SongBuilder setId(int id) {
        this.id = id;
        return this;
    }

    public Song createSong() {
        return new Song(id, title, artist, label, released);
    }
}