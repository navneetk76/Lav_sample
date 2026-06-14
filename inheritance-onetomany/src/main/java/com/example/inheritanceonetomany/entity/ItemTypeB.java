package com.example.inheritanceonetomany.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

// ================================================================
// TODO: Rename to your second subtype.
//   Examples: Truck, StandardProduct, RoutineTask ...
// ================================================================
@Entity
@DiscriminatorValue("TYPE_B")     // TODO: change to a meaningful value, e.g. "TRUCK"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemTypeB extends Item {

    @Column(name = "extra_field_b")
    private String extraFieldB;    // TODO: rename

    private Integer numericField;  // TODO: rename  (avoid 'value' — reserved keyword)
}
