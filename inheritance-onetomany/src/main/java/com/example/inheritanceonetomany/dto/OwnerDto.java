package com.example.inheritanceonetomany.dto;

import lombok.*;

import java.util.List;

// TODO: Add / remove fields to match your Owner entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class OwnerDto {
    private Long         id;
    private String       name;
    private String       category;
    private Integer      entryYear;
    private List<ItemDto> items;     // mixed TypeA + TypeB
}
