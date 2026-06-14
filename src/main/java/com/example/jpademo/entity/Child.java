package com.example.jpademo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

// ================================================================
// TODO (Step 4b): Rename this class, table name, and fields to
//   match your domain (e.g. Song, Employee, Product …).
//   This is the MANY side of ONE-TO-MANY with Parent,
//   and the ONE side of the ONE-TO-ONE with ChildDetail.
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

    private String dataValue;      // TODO: rename  (renamed from 'value' — reserved keyword)

    private Integer entryYear;     // TODO: rename  (renamed from 'year' — reserved keyword)

    // ---------------------------------------------------------------
    // MANY-TO-ONE (owning side of the ONE-TO-MANY with Parent):
    //   @JoinColumn places the parent_id FK in THIS (child) table.
    // ---------------------------------------------------------------
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    // ---------------------------------------------------------------
    // ONE-TO-ONE (inverse / non-owning side):
    //   mappedBy = "child" points to the 'child' field in ChildDetail.
    //   cascade ALL so detail is saved/deleted with this Child.
    //   orphanRemoval removes the detail row when unlinked.
    // ---------------------------------------------------------------
    @OneToOne(mappedBy = "child", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private ChildDetail detail;

    // Keep both sides of the one-to-one relationship in sync
    public void setDetail(ChildDetail detail) {
        if (detail == null) {
            if (this.detail != null) {
                this.detail.setChild(null);
            }
        } else {
            detail.setChild(this);
        }
        this.detail = detail;
    }

    @Override
    public String toString() {
        return "Child{id=" + id + ", name='" + name + "'}";
    }
}
