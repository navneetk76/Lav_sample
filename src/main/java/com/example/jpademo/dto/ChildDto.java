package com.example.jpademo.dto;

import lombok.*;

// TODO: Add / remove fields to match your Child entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ChildDto {
    private Long id;
    private String name;
    private String dataValue;
    private Integer entryYear;
    private Long parentId;
    private String parentName;
    private ChildDetailDto detail;
}
