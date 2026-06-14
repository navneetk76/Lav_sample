package com.example.jpademo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

// TODO: Add / remove fields to match your Child entity
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class CreateChildRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String dataValue;

    private Integer entryYear;

    @NotNull(message = "Parent ID is required")
    private Long parentId;

    // Optional: detail can be added later via PUT /children/{id}/detail
    private ChildDetailDto detail;
}
