package com.example.artistsongs.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDetailDto {
    private Long id;
    private String album;
    private Integer durationSeconds;
    private String genre;
    private String lyricsSnippet;
}
