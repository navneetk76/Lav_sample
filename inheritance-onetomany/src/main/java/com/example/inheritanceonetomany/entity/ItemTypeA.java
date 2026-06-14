package com.example.inheritanceonetomany.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

// ================================================================
// TODO: Rename to your first subtype.
//   Examples: Car, PremiumProduct, UrgentTask ...
// ================================================================
@Entity
@DiscriminatorValue("TYPE_A")     // TODO: change to a meaningful value, e.g. "CAR"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemTypeA extends Item {

    // Explicit column name because Hibernate maps 'extraFieldA' -> 'extra_fielda'
    @Column(name = "extra_field_a")
    private String extraFieldA;    // TODO: rename
}
