package com.example.inheritanceonetomany.dto;

import lombok.*;

// Flat DTO covering all subtypes. itemType identifies the subclass.
// TODO: Add / remove fields to match your entity hierarchy.
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemDto {
    private Long    id;
    private String  itemType;       // "TYPE_A" or "TYPE_B"
    private String  name;
    private String  description;
    private Integer entryYear;
    private Long    ownerId;
    private String  ownerName;

    // TypeA-specific (null for TypeB rows)
    private String  extraFieldA;

    // TypeB-specific (null for TypeA rows)
    private String  extraFieldB;
    private Integer numericField;
}
