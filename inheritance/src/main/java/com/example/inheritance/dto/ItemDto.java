package com.example.inheritance.dto;

import lombok.*;

// ================================================================
// Flat DTO that covers ALL subtypes.
// itemType tells the client which subclass it received.
// Subtype-specific fields are null when they don't apply.
// TODO: Add / remove fields to match your entity hierarchy.
// ================================================================
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ItemDto {
    private Long    id;
    private String  itemType;       // "TYPE_A" or "TYPE_B"
    private String  name;
    private String  description;
    private Integer entryYear;

    // TypeA-specific (null for TypeB rows)
    private String  extraFieldA;

    // TypeB-specific (null for TypeA rows)
    private String  extraFieldB;
    private Integer numericField;
}
