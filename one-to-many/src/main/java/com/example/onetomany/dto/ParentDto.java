package com.example.onetomany.dto;

import lombok.*;

import java.util.List;

// TODO: Add / remove fields to match your Parent entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ParentDto {
    private Long id;
    private String name;
    private String category;
    private String description;
    private Integer entryYear;
    private List<ChildDto> children;
}
