package com.example.inheritance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// TODO: Add / remove fields to match your TypeB entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateItemTypeBRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    private Integer entryYear;

    private String extraFieldB;    // TypeB-specific

    private Integer numericField;  // TypeB-specific
}
