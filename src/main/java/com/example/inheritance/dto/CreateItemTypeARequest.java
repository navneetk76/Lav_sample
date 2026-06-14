package com.example.inheritance.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

// TODO: Add / remove fields to match your TypeA entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateItemTypeARequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String address;

    private Integer birthYear;
    private Boolean isMale;

    private String studentClass;    // TypeA-specific
}
