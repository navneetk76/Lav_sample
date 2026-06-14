package com.example.onetomany.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// ================================================================
// TODO: Rename this class, table name, and fields to your domain.
//   Examples: Department, Category, Team, Course, Author ...
//   This is the ONE side of the ONE-TO-MANY relationship.
// ================================================================
@Entity
@Table(name = "parent")           // TODO: rename table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Parent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;           // TODO: rename / add domain fields

    private String category;       // TODO: rename  (avoid 'type' — reserved keyword)

    private String description;    // TODO: rename

    private Integer entryYear;     // TODO: rename  (avoid 'year' — reserved keyword)

    // ---------------------------------------------------------------
    // ONE-TO-MANY: One Parent -> Many Children
    //
    //   mappedBy      = the field name in Child that holds the FK
    //   cascade       = save/delete on Parent propagates to Children
    //   orphanRemoval = removes Child rows that are no longer in this list
    //   fetch LAZY    = children are only loaded when accessed (default)
    // ---------------------------------------------------------------
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Child> children = new ArrayList<>();

    // Keep BOTH sides of the bidirectional relationship in sync
    public void addChild(Child child) {
        children.add(child);
        child.setParent(this);
    }

    public void removeChild(Child child) {
        children.remove(child);
        child.setParent(null);
    }

    @Override
    public String toString() {
        return "Parent{id=" + id + ", name='" + name + "'}";
    }
}
