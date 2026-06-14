package com.example.artistsongs.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * MANY side of the ONE-TO-MANY relationship with Artist.
 * ONE side of the ONE-TO-ONE relationship with SongDetail.
 */
@Entity
@Table(name = "song")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String title;

    private Integer releaseYear;

    // -------------------------------------------------------
    // MANY-TO-ONE (many songs → one artist)
    //   - @JoinColumn places the artist_id FK in this (song) table
    //   - This is the "owning" side of the bidirectional relationship
    // -------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "artist_id", nullable = false)
    private Artist artist;

    // -------------------------------------------------------
    // ONE-TO-ONE (inverse/non-owning side)
    //   - mappedBy = "song" points to the 'song' field in SongDetail
    //   - cascade ALL so detail is saved/deleted with the song
    //   - orphanRemoval removes the detail row when unlinked
    // -------------------------------------------------------
    @OneToOne(mappedBy = "song", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private SongDetail detail;

    // Convenience helper to keep both sides in sync
    public void setDetail(SongDetail detail) {
        if (detail == null) {
            if (this.detail != null) {
                this.detail.setSong(null);
            }
        } else {
            detail.setSong(this);
        }
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Song{id=" + id + ", title='" + title + "', releaseYear=" + releaseYear + "}";
    }
}
