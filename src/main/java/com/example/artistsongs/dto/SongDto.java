package com.example.artistsongs.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDto {
    private Long id;
    private String title;
    private Integer releaseYear;
    private Long artistId;
    private String artistName;
    private SongDetailDto detail;
}
