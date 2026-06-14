package com.example.onetomany.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// ================================================================
// TODO: Rename this class, table name, and fields to your domain.
//   Examples: Employee, Product, Song, Student, Order ...
//   This is the MANY side of the ONE-TO-MANY relationship.
//   The FK column (parent_id) lives in THIS table.
// ================================================================
@Entity
@Table(name = "child")            // TODO: rename table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Child {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;           // TODO: rename / add domain fields

    private String dataValue;      // TODO: rename  (avoid 'value' — reserved keyword)

    private Integer entryYear;     // TODO: rename  (avoid 'year' — reserved keyword)

    // ---------------------------------------------------------------
    // MANY-TO-ONE: Many Children -> One Parent
    //
    //   This is the OWNING side of the relationship.
    //   @JoinColumn places the FK column (parent_id) in THIS table.
    //   fetch LAZY = parent is only loaded when accessed
    // ---------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @Override
    public String toString() {
        return "Child{id=" + id + ", name='" + name + "'}";
    }
}
