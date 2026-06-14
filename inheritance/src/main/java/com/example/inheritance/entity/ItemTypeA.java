package com.example.inheritance.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

// ================================================================
// TODO: Rename to your first subtype.
//   Examples: Car, CreditCardPayment, Manager, Circle, Dog ...
// ================================================================
@Entity
@DiscriminatorValue("TYPE_A")     // TODO: change to a meaningful string, e.g. "CAR"
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ItemTypeA extends Item {

    // Fields specific to TypeA — other subtypes will have NULL for these columns.
    // @Column name is explicit because Hibernate maps 'extraFieldA' -> 'extra_fielda'
    // (single trailing letter isn't separated), so we pin it manually.
    @Column(name = "extra_field_a")
    private String extraFieldA;    // TODO: rename to a meaningful field, e.g. numDoors
}
