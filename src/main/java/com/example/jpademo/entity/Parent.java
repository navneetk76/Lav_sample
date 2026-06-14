package com.example.jpademo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// ================================================================
// TODO (Step 4a): Rename this class, table name, and fields to
//   match your domain (e.g. Artist, Department, Category …).
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

    private String category;       // TODO: rename  (renamed from 'type' — reserved keyword)

    private String description;    // TODO: rename

    private Integer entryYear;     // TODO: rename  (renamed from 'year' — reserved keyword)

    // ---------------------------------------------------------------
    // ONE-TO-MANY: One Parent → Many Children
    //   mappedBy   = field name in Child that holds the FK
    //   cascade    = operations on Parent propagate to Children
    //   orphanRemoval = delete Child rows removed from this list
    // ---------------------------------------------------------------
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Child> children = new ArrayList<>();

    // Keep both sides of the bidirectional relationship in sync
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
