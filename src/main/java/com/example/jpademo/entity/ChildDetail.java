package com.example.jpademo.entity;

import jakarta.persistence.*;
import lombok.*;

// ================================================================
// TODO (Step 4c): Rename this class, table name, and fields to
//   match your domain (e.g. SongDetail, EmployeeProfile, Address …).
//   This is the OWNED side of the ONE-TO-ONE relationship.
//   The FK column (child_id) lives in THIS table.
// ================================================================
@Entity
@Table(name = "child_detail")     // TODO: rename table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------------------------------------------------------------
    // ONE-TO-ONE (owned / FK side):
    //   @JoinColumn places the child_id FK in this table.
    //   unique = true enforces the one-to-one constraint at DB level.
    // ---------------------------------------------------------------
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", unique = true, nullable = false)
    private Child child;

    private String description;     // TODO: rename / add domain fields

    private String additionalInfo;  // TODO: rename

    private Integer numericValue;   // TODO: rename

    @Column(length = 512)
    private String notes;           // TODO: rename

    @Override
    public String toString() {
        return "ChildDetail{id=" + id + ", description='" + description + "'}";
    }
}
