package com.example.inheritanceonetomany.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateOwnerRequest {
    @NotBlank(message = "Name is required")
    private String name;
    private String category;
    private Integer entryYear;
}
