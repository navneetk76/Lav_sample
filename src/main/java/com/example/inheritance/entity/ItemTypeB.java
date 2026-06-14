package com.example.inheritance.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

// ================================================================
// TODO: Rename to your second subtype.
//   Examples: Truck, BankTransfer, Director, Rectangle, Cat ...
// ================================================================
@Entity
@DiscriminatorValue("TEACHER")     // TODO: change to a meaningful string, e.g. "TRUCK"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemTypeB extends Item {

    // Fields specific to TypeB
    @Column(name = "extra_field_b")
    private String qualification;    // TODO: rename
    @Column(name = "extra_field_b2")
    private Integer experienceYrs;  // TODO: rename  (avoid 'value' — reserved keyword)
}
