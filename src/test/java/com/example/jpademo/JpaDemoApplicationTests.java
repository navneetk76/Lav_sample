package com.example.jpademo;

import com.example.jpademo.dto.*;
import com.example.jpademo.service.ChildService;
import com.example.jpademo.service.ParentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class JpaDemoApplicationTests {

    @Autowired ParentService parentService;
    @Autowired ChildService childService;

    @Test
    void contextLoads() {
    }

    // ---------------------------------------------------------------
    // ONE-TO-MANY tests
    // ---------------------------------------------------------------

    @Test
    void oneToMany_parentHasManyChildren() {
        // Seed data: Parent 1 has 3 children
        ParentDto parent = parentService.findById(1L);
        assertThat(parent.getName()).isEqualTo("Parent One");
        assertThat(parent.getChildren()).hasSize(3);
    }

    @Test
    void oneToMany_createParentAndLinkChildren() {
        ParentDto parent = parentService.create(
                new CreateParentRequest("New Parent", "Type X", "Desc", 2024));
        assertThat(parent.getId()).isNotNull();

        ChildDto child = childService.create(
                new CreateChildRequest("New Child", "val-x", 2024, parent.getId(), null));
        assertThat(child.getParentId()).isEqualTo(parent.getId());
        assertThat(child.getParentName()).isEqualTo("New Parent");

        ParentDto updated = parentService.findById(parent.getId());
        assertThat(updated.getChildren()).hasSize(1);
        assertThat(updated.getChildren().get(0).getName()).isEqualTo("New Child");
    }

    @Test
    void oneToMany_deleteCascadesToChildren() {
        ParentDto parent = parentService.create(
                new CreateParentRequest("Temp Parent", "Type T", "Desc", 2020));
        childService.create(new CreateChildRequest("Temp Child 1", "v1", 2021, parent.getId(), null));
        childService.create(new CreateChildRequest("Temp Child 2", "v2", 2022, parent.getId(), null));

        assertThat(childService.findByParent(parent.getId())).hasSize(2);

        parentService.delete(parent.getId());

        assertThat(childService.findByParent(parent.getId())).isEmpty();
    }

    // ---------------------------------------------------------------
    // ONE-TO-ONE tests
    // ---------------------------------------------------------------

    @Test
    void oneToOne_childHasDetail() {
        // Seed data: Child 1 has a ChildDetail
        ChildDto child = childService.findById(1L);
        assertThat(child.getName()).isEqualTo("Child 1-A");
        assertThat(child.getDetail()).isNotNull();
        assertThat(child.getDetail().getDescription()).isEqualTo("Detail for Child 1-A");
        assertThat(child.getDetail().getNumericValue()).isEqualTo(100);
    }

    @Test
    void oneToOne_addDetailToChildWithoutOne() {
        ChildDto child = childService.create(
                new CreateChildRequest("No-Detail Child", "val", 2024, 1L, null));
        assertThat(child.getDetail()).isNull();

        ChildDetailDto detailDto = ChildDetailDto.builder()
                .description("Added later")
                .additionalInfo("Extra")
                .numericValue(42)
                .notes("Some notes")
                .build();
        ChildDto updated = childService.upsertDetail(child.getId(), detailDto);

        assertThat(updated.getDetail()).isNotNull();
        assertThat(updated.getDetail().getDescription()).isEqualTo("Added later");
        assertThat(updated.getDetail().getNumericValue()).isEqualTo(42);
    }

    @Test
    void oneToOne_upsertReplacesExistingDetail() {
        ChildDto child = childService.findById(1L);
        assertThat(child.getDetail().getNumericValue()).isEqualTo(100);

        ChildDetailDto newDetail = ChildDetailDto.builder()
                .description("Replaced description")
                .additionalInfo("New info")
                .numericValue(999)
                .notes("Updated notes")
                .build();
        ChildDto updated = childService.upsertDetail(child.getId(), newDetail);

        assertThat(updated.getDetail().getNumericValue()).isEqualTo(999);
        assertThat(updated.getDetail().getDescription()).isEqualTo("Replaced description");
    }
}
