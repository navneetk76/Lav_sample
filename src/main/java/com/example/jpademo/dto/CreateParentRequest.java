package com.example.jpademo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// TODO: Add / remove fields to match your Parent entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateParentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String category;

    private String description;

    private Integer entryYear;
}
