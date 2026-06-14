package com.example.onetomany;

import com.example.onetomany.dto.ChildDto;
import com.example.onetomany.dto.CreateChildRequest;
import com.example.onetomany.dto.CreateParentRequest;
import com.example.onetomany.dto.ParentDto;
import com.example.onetomany.exception.ResourceNotFoundException;
import com.example.onetomany.service.ChildService;
import com.example.onetomany.service.ParentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class OneManyApplicationTests {

    @Autowired ParentService parentService;
    @Autowired ChildService childService;

    @Test
    void contextLoads() {
    }

    // ---------------------------------------------------------------
    // Read — verify seed data loaded correctly
    // ---------------------------------------------------------------

    @Test
    void seedData_parentOneHasThreeChildren() {
        ParentDto parent = parentService.findById(1L);
        assertThat(parent.getName()).isEqualTo("Parent One");
        assertThat(parent.getChildren()).hasSize(3);
    }

    @Test
    void seedData_allParentsLoadedWithChildren() {
        List<ParentDto> parents = parentService.findAll();
        assertThat(parents).hasSize(3);
        // Each parent has children
        parents.forEach(p -> assertThat(p.getChildren()).isNotEmpty());
    }

    @Test
    void seedData_childKnowsItsParent() {
        ChildDto child = childService.findById(1L);
        assertThat(child.getName()).isEqualTo("Child 1-A");
        assertThat(child.getParentId()).isEqualTo(1L);
        assertThat(child.getParentName()).isEqualTo("Parent One");
    }

    // ---------------------------------------------------------------
    // Create
    // ---------------------------------------------------------------

    @Test
    void create_parentThenChild() {
        // Create parent
        ParentDto parent = parentService.create(
                new CreateParentRequest("New Parent", "Type X", "Desc", 2024));
        assertThat(parent.getId()).isNotNull();
        assertThat(parent.getChildren()).isEmpty();

        // Add a child linked to the new parent
        ChildDto child = childService.create(
                new CreateChildRequest("New Child", "val-x", 2024, parent.getId()));
        assertThat(child.getParentId()).isEqualTo(parent.getId());
        assertThat(child.getParentName()).isEqualTo("New Parent");

        // Parent now reports the child in its list
        ParentDto updated = parentService.findById(parent.getId());
        assertThat(updated.getChildren()).hasSize(1);
        assertThat(updated.getChildren().get(0).getName()).isEqualTo("New Child");
    }

    @Test
    void create_multipleChildrenForOneParent() {
        ParentDto parent = parentService.create(
                new CreateParentRequest("Multi-Child Parent", "Type M", null, 2022));

        childService.create(new CreateChildRequest("Child A", "va", 2022, parent.getId()));
        childService.create(new CreateChildRequest("Child B", "vb", 2023, parent.getId()));
        childService.create(new CreateChildRequest("Child C", "vc", 2024, parent.getId()));

        ParentDto loaded = parentService.findById(parent.getId());
        assertThat(loaded.getChildren()).hasSize(3);
    }

    // ---------------------------------------------------------------
    // Update
    // ---------------------------------------------------------------

    @Test
    void update_childFields() {
        ChildDto original = childService.findById(1L);
        assertThat(original.getName()).isEqualTo("Child 1-A");

        ChildDto updated = childService.update(1L,
                new CreateChildRequest("Renamed Child", "new-val", 2025, original.getParentId()));

        assertThat(updated.getName()).isEqualTo("Renamed Child");
        assertThat(updated.getDataValue()).isEqualTo("new-val");
        assertThat(updated.getEntryYear()).isEqualTo(2025);
    }

    @Test
    void update_reparentChild() {
        // Move child 4 from parent 2 to parent 1
        ChildDto before = childService.findById(4L);
        assertThat(before.getParentId()).isEqualTo(2L);

        ChildDto after = childService.update(4L,
                new CreateChildRequest(before.getName(), before.getDataValue(), before.getEntryYear(), 1L));

        assertThat(after.getParentId()).isEqualTo(1L);

        // Parent 1 now has 4 children, parent 2 has 1
        assertThat(parentService.findById(1L).getChildren()).hasSize(4);
        assertThat(parentService.findById(2L).getChildren()).hasSize(1);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    @Test
    void delete_childOnly() {
        List<ChildDto> before = childService.findByParent(1L);
        assertThat(before).hasSize(3);

        childService.delete(1L);

        List<ChildDto> after = childService.findByParent(1L);
        assertThat(after).hasSize(2);
    }

    @Test
    void delete_parentCascadesToAllChildren() {
        ParentDto parent = parentService.create(
                new CreateParentRequest("Temp Parent", "T", null, 2020));
        childService.create(new CreateChildRequest("Temp C1", "v1", 2021, parent.getId()));
        childService.create(new CreateChildRequest("Temp C2", "v2", 2022, parent.getId()));

        assertThat(childService.findByParent(parent.getId())).hasSize(2);

        parentService.delete(parent.getId());

        // After deleting parent, querying its children returns empty
        assertThat(childService.findByParent(parent.getId())).isEmpty();
    }

    @Test
    void delete_parentNotFound_throws() {
        assertThatThrownBy(() -> parentService.delete(9999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("9999");
    }

    // ---------------------------------------------------------------
    // Query by parent
    // ---------------------------------------------------------------

    @Test
    void query_childrenByParentId() {
        List<ChildDto> children = childService.findByParent(2L);
        assertThat(children).hasSize(2);
        children.forEach(c -> assertThat(c.getParentId()).isEqualTo(2L));
    }
}
