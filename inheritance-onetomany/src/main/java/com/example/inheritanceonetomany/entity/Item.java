package com.example.inheritanceonetomany.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

// ================================================================
// TODO: Rename this abstract class to your base type.
//   Examples: Vehicle, Product, Task, Asset ...
//
// This class combines TWO JPA patterns:
//   1. INHERITANCE (SINGLE_TABLE) — subtypes share this table
//   2. MANY-TO-ONE to Owner     — this is the FK (owning) side
//
// The item table contains columns from BOTH patterns:
//   - Base fields: id, item_type, name, description, entry_year
//   - TypeA fields: extra_field_a
//   - TypeB fields: extra_field_b, numeric_field
//   - FK column:    owner_id
// ================================================================
@Entity
@Table(name = "item")             // TODO: rename table
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;           // TODO: rename

    private String description;    // TODO: rename

    private Integer entryYear;     // TODO: rename  (avoid 'year' — reserved keyword)

    // ---------------------------------------------------------------
    // MANY-TO-ONE (owning side of the ONE-TO-MANY with Owner):
    //   The owner_id FK column lives in THIS table.
    //   Every subtype row (TypeA, TypeB) has this FK.
    // ---------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;
}
