package com.example.artistsongs.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistDto {
    private Long id;
    private String name;
    private String nationality;
    private String genre;
    private Integer debutYear;
    private List<SongDto> songs;
}
