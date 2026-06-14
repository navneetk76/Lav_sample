package com.example.artistsongs.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ONE-TO-ONE "owned" side.
 * Each Song has exactly ONE SongDetail record.
 * The foreign key (song_id) lives in this table.
 */
@Entity
@Table(name = "song_detail")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // -------------------------------------------------------
    // ONE-TO-ONE (owned side): SongDetail owns the FK column.
    //   - @JoinColumn places song_id FK in this table
    //   - unique = true enforces the one-to-one constraint at DB level
    // -------------------------------------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", unique = true, nullable = false)
    private Song song;

    private String album;

    private Integer durationSeconds;

    private String genre;

    @Column(length = 512)
    private String lyricsSnippet;

    @Override
    public String toString() {
        return "SongDetail{id=" + id + ", album='" + album + "', durationSeconds=" + durationSeconds + "}";
    }
}
