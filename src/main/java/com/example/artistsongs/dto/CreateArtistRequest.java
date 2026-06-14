package com.example.artistsongs.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreateArtistRequest {

    @NotBlank(message = "Artist name is required")
    private String name;

    private String nationality;

    private String genre;

    @Positive(message = "Debut year must be positive")
    private Integer debutYear;
}
