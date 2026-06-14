package com.example.artistsongs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ONE side of the ONE-TO-MANY relationship.
 * One Artist can have MANY Songs.
 */
@Entity
@Table(name = "artist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Artist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String nationality;

    private String genre;

    @Positive
    private Integer debutYear;

    // -------------------------------------------------------
    // ONE-TO-MANY: One Artist → Many Songs
    //   - mappedBy = "artist" refers to the 'artist' field in Song
    //   - cascade ALL so songs are created/deleted with the artist
    //   - orphanRemoval ensures removed songs are deleted from DB
    // -------------------------------------------------------
    @OneToMany(mappedBy = "artist", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Song> songs = new ArrayList<>();

    // Convenience helpers to keep both sides of the relationship in sync
    public void addSong(Song song) {
        songs.add(song);
        song.setArtist(this);
    }

    public void removeSong(Song song) {
        songs.remove(song);
        song.setArtist(null);
    }

    @Override
    public String toString() {
        return "Artist{id=" + id + ", name='" + name + "', genre='" + genre + "'}";
    }
}
