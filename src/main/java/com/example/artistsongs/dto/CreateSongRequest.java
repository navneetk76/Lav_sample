package com.example.artistsongs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateSongRequest {

    @NotBlank(message = "Song title is required")
    private String title;

    private Integer releaseYear;

    @NotNull(message = "Artist ID is required")
    private Long artistId;

    // Optional detail — can be added later via PATCH /songs/{id}/detail
    private SongDetailDto detail;
}
