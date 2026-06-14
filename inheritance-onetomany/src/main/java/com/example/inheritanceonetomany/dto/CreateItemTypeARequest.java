package com.example.inheritanceonetomany.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateItemTypeARequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String description;
    private Integer entryYear;
    @NotNull(message = "Owner ID is required")
    private Long ownerId;
    private String extraFieldA;
}
