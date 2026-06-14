package com.example.inheritance.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

// ================================================================
// TODO: Rename this abstract class to your base type.
//   Examples: Vehicle, Payment, Employee, Shape, Animal ...
//
// JPA INHERITANCE STRATEGY — choose ONE:
//
//   SINGLE_TABLE  (used here)
//     • All subclasses share ONE table.
//     • A discriminator column (item_type) tells Hibernate
//       which Java subclass each row maps to.
//     • Pros: fast queries (no joins), simple schema.
//     • Cons: nullable columns for subtype-specific fields.
//
//   JOINED        (normalised alternative)
//     • Replace the two annotations below with:
//         @Inheritance(strategy = InheritanceType.JOINED)
//     • Each class gets its own table; queries use JOIN.
//     • Pros: no nulls, normalised.
//     • Cons: extra JOIN on every read.
//
//   TABLE_PER_CLASS
//     • @Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
//     • Each concrete class gets its own stand-alone table.
//     • Pros: no joins, no nulls.
//     • Cons: no shared sequence; polymorphic queries use UNION ALL.
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

    // NOTE: Do NOT map the discriminator column as a @Column field here.
    // Access the subtype name at the DTO layer via instanceof (see ItemService).
}
