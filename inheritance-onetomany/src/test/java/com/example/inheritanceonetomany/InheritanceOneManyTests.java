package com.example.inheritanceonetomany;

import com.example.inheritanceonetomany.dto.*;
import com.example.inheritanceonetomany.entity.Item;
import com.example.inheritanceonetomany.entity.ItemTypeA;
import com.example.inheritanceonetomany.entity.ItemTypeB;
import com.example.inheritanceonetomany.exception.ResourceNotFoundException;
import com.example.inheritanceonetomany.repository.ItemRepository;
import com.example.inheritanceonetomany.service.ItemService;
import com.example.inheritanceonetomany.service.OwnerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class InheritanceOneManyTests {

    @Autowired OwnerService   ownerService;
    @Autowired ItemService    itemService;
    @Autowired ItemRepository itemRepository;

    @Test
    void contextLoads() {
    }

    // ---------------------------------------------------------------
    // Seed data
    // ---------------------------------------------------------------

    @Test
    void seedData_threeOwners() {
        assertThat(ownerService.findAll()).hasSize(3);
    }

    @Test
    void seedData_eightItemsTotal() {
        assertThat(itemService.findAll()).hasSize(8);
    }

    @Test
    void seedData_ownerOneHasThreeItems() {
        OwnerDto owner = ownerService.findById(1L);
        assertThat(owner.getItems()).hasSize(3);
        // Mix of types
        assertThat(owner.getItems()).anyMatch(i -> "TYPE_A".equals(i.getItemType()));
        assertThat(owner.getItems()).anyMatch(i -> "TYPE_B".equals(i.getItemType()));
    }

    // ---------------------------------------------------------------
    // Inheritance — correct subtype returned
    // ---------------------------------------------------------------

    @Test
    void inheritance_repositoryReturnsCorrectSubtype() {
        List<Item> all = itemRepository.findAll();
        assertThat(all).anyMatch(i -> i instanceof ItemTypeA);
        assertThat(all).anyMatch(i -> i instanceof ItemTypeB);
    }

    @Test
    void inheritance_subtypeFieldsAccessible() {
        // Item 1 is TYPE_A
        ItemDto typeA = itemService.findById(1L);
        assertThat(typeA.getItemType()).isEqualTo("TYPE_A");
        assertThat(typeA.getExtraFieldA()).isEqualTo("alpha-1");
        assertThat(typeA.getExtraFieldB()).isNull();

        // Item 3 is TYPE_B
        ItemDto typeB = itemService.findById(3L);
        assertThat(typeB.getItemType()).isEqualTo("TYPE_B");
        assertThat(typeB.getExtraFieldB()).isEqualTo("beta-1");
        assertThat(typeB.getNumericField()).isEqualTo(100);
        assertThat(typeB.getExtraFieldA()).isNull();
    }

    // ---------------------------------------------------------------
    // One-to-Many — query items by owner
    // ---------------------------------------------------------------

    @Test
    void oneToMany_findAllItemsByOwner() {
        List<ItemDto> owner1Items = itemService.findByOwner(1L);
        assertThat(owner1Items).hasSize(3);
        owner1Items.forEach(i -> assertThat(i.getOwnerId()).isEqualTo(1L));
    }

    @Test
    void oneToMany_filterByOwnerAndSubtype() {
        // Owner 2 has 1 TypeA and 2 TypeB
        assertThat(itemService.findTypeAByOwner(2L)).hasSize(1);
        assertThat(itemService.findTypeBByOwner(2L)).hasSize(2);
    }

    // ---------------------------------------------------------------
    // Create — combines both patterns
    // ---------------------------------------------------------------

    @Test
    void create_typeALinkedToOwner() {
        CreateItemTypeARequest req = new CreateItemTypeARequest(
                "New A", "desc", 2024, 1L, "my-extra");
        ItemDto created = itemService.createTypeA(req);

        assertThat(created.getItemType()).isEqualTo("TYPE_A");
        assertThat(created.getOwnerId()).isEqualTo(1L);
        assertThat(created.getExtraFieldA()).isEqualTo("my-extra");

        // Owner's item list now has 4 items
        assertThat(ownerService.findById(1L).getItems()).hasSize(4);
    }

    @Test
    void create_typeBLinkedToOwner() {
        CreateItemTypeBRequest req = new CreateItemTypeBRequest(
                "New B", "desc", 2024, 2L, "my-extra-b", 99);
        ItemDto created = itemService.createTypeB(req);

        assertThat(created.getItemType()).isEqualTo("TYPE_B");
        assertThat(created.getOwnerId()).isEqualTo(2L);
        assertThat(created.getNumericField()).isEqualTo(99);
    }

    // ---------------------------------------------------------------
    // Delete — cascade from owner deletes all subtypes
    // ---------------------------------------------------------------

    @Test
    void delete_ownerCascadesToAllSubtypes() {
        // Owner 1 has items 1 (TypeA), 2 (TypeA), 3 (TypeB)
        assertThat(itemService.findByOwner(1L)).hasSize(3);

        ownerService.delete(1L);

        assertThat(itemService.findByOwner(1L)).isEmpty();
        // Total items reduced by 3
        assertThat(itemService.findAll()).hasSize(5);
    }

    @Test
    void delete_singleItemAnySubtype() {
        itemService.delete(1L);  // TypeA
        itemService.delete(3L);  // TypeB
        assertThat(itemService.findAll()).hasSize(6);
    }

    @Test
    void delete_notFound_throws() {
        assertThatThrownBy(() -> itemService.delete(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
