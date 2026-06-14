package com.example.inheritanceonetomany.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

// ================================================================
// TODO: Rename to your "one" side entity.
//   Examples: Department, Library, Category, Author ...
//
// ONE-TO-MANY relationship:
//   One Owner -> Many Items  (items can be TypeA or TypeB)
// ================================================================
@Entity
@Table(name = "owner")            // TODO: rename table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Owner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String name;           // TODO: rename

    private String category;       // TODO: rename  (avoid 'type' — reserved keyword)

    private Integer entryYear;     // TODO: rename  (avoid 'year' — reserved keyword)

    // ---------------------------------------------------------------
    // ONE-TO-MANY: One Owner -> Many Items (of any subtype)
    //   mappedBy      = 'owner' field in the Item base class
    //   cascade ALL   = save/delete propagates to all items
    //   orphanRemoval = removes items dropped from this list
    // ---------------------------------------------------------------
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Item> items = new ArrayList<>();

    // Keep both sides of the bidirectional relationship in sync
    public void addItem(Item item) {
        items.add(item);
        item.setOwner(this);
    }

    public void removeItem(Item item) {
        items.remove(item);
        item.setOwner(null);
    }

    @Override
    public String toString() {
        return "Owner{id=" + id + ", name='" + name + "'}";
    }
}
