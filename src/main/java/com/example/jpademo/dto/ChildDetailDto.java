package com.example.jpademo.dto;

import lombok.*;

// TODO: Add / remove fields to match your ChildDetail entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChildDetailDto {
    private Long id;
    private String description;
    private String additionalInfo;
    private Integer numericValue;
    private String notes;
}
