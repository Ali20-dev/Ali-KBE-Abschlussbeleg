package htwb.ai.ALIS.model;


import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table
@JacksonXmlRootElement
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 100, nullable = false)
    @NotNull
    private String title;

    @Column(length = 100)
    private String artist;

    @Column(length = 100)
    private String label;

    @Column
    private Integer released;

    public Song() {

    }

    public Song(Integer id, @NotNull String title, String artist, String label, Integer released) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.label = label;
        this.released = released;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Integer getReleased() {
        return released;
    }

    public void setReleased(Integer released) {
        this.released = released;
    }
}
