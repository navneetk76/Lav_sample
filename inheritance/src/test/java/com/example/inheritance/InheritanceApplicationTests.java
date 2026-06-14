package com.example.inheritance;

import com.example.inheritance.dto.CreateItemTypeARequest;
import com.example.inheritance.dto.CreateItemTypeBRequest;
import com.example.inheritance.dto.ItemDto;
import com.example.inheritance.entity.Item;
import com.example.inheritance.entity.ItemTypeA;
import com.example.inheritance.entity.ItemTypeB;
import com.example.inheritance.exception.ResourceNotFoundException;
import com.example.inheritance.repository.ItemRepository;
import com.example.inheritance.service.ItemService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class InheritanceApplicationTests {

    @Autowired ItemService   itemService;
    @Autowired ItemRepository itemRepository;

    @Test
    void contextLoads() {
    }

    // ---------------------------------------------------------------
    // Verify seed data
    // ---------------------------------------------------------------

    @Test
    void seedData_sixItemsTotal() {
        assertThat(itemService.findAll()).hasSize(6);
    }

    @Test
    void seedData_threeOfEachSubtype() {
        assertThat(itemService.findAllTypeA()).hasSize(3);
        assertThat(itemService.findAllTypeB()).hasSize(3);
    }

    @Test
    void seedData_discriminatorPopulated() {
        List<ItemDto> all = itemService.findAll();
        assertThat(all).allMatch(dto -> dto.getItemType() != null);
        assertThat(all).anyMatch(dto -> "TYPE_A".equals(dto.getItemType()));
        assertThat(all).anyMatch(dto -> "TYPE_B".equals(dto.getItemType()));
    }

    // ---------------------------------------------------------------
    // Polymorphic persistence — correct Java type returned
    // ---------------------------------------------------------------

    @Test
    void inheritance_repositoryReturnsCorrectSubtype() {
        List<Item> all = itemRepository.findAll();
        long typeACount = all.stream().filter(i -> i instanceof ItemTypeA).count();
        long typeBCount = all.stream().filter(i -> i instanceof ItemTypeB).count();
        assertThat(typeACount).isEqualTo(3);
        assertThat(typeBCount).isEqualTo(3);
    }

    @Test
    void inheritance_subtypeFieldsAccessibleAfterLoad() {
        // TypeA item should expose extraFieldA
        ItemDto typeA = itemService.findById(1L);
        assertThat(typeA.getItemType()).isEqualTo("TYPE_A");
        assertThat(typeA.getExtraFieldA()).isEqualTo("alpha-value");
        assertThat(typeA.getExtraFieldB()).isNull();     // TypeB field is null
        assertThat(typeA.getNumericField()).isNull();

        // TypeB item should expose extraFieldB + numericField
        ItemDto typeB = itemService.findById(4L);
        assertThat(typeB.getItemType()).isEqualTo("TYPE_B");
        assertThat(typeB.getExtraFieldB()).isEqualTo("extra-b-1");
        assertThat(typeB.getNumericField()).isEqualTo(100);
        assertThat(typeB.getExtraFieldA()).isNull();     // TypeA field is null
    }

    // ---------------------------------------------------------------
    // Create subtypes
    // ---------------------------------------------------------------

    @Test
    void create_typeA() {
        CreateItemTypeARequest req = new CreateItemTypeARequest(
                "New TypeA", "Desc A", 2024, "my-extra-a");
        ItemDto created = itemService.createTypeA(req);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getItemType()).isEqualTo("TYPE_A");
        assertThat(created.getName()).isEqualTo("New TypeA");
        assertThat(created.getExtraFieldA()).isEqualTo("my-extra-a");
        assertThat(created.getExtraFieldB()).isNull();
    }

    @Test
    void create_typeB() {
        CreateItemTypeBRequest req = new CreateItemTypeBRequest(
                "New TypeB", "Desc B", 2024, "my-extra-b", 42);
        ItemDto created = itemService.createTypeB(req);

        assertThat(created.getId()).isNotNull();
        assertThat(created.getItemType()).isEqualTo("TYPE_B");
        assertThat(created.getName()).isEqualTo("New TypeB");
        assertThat(created.getExtraFieldB()).isEqualTo("my-extra-b");
        assertThat(created.getNumericField()).isEqualTo(42);
        assertThat(created.getExtraFieldA()).isNull();
    }

    @Test
    void create_allTypesCountCorrect() {
        itemService.createTypeA(new CreateItemTypeARequest("A+", "d", 2024, "v"));
        itemService.createTypeB(new CreateItemTypeBRequest("B+", "d", 2024, "v", 1));

        assertThat(itemService.findAllTypeA()).hasSize(4);
        assertThat(itemService.findAllTypeB()).hasSize(4);
        assertThat(itemService.findAll()).hasSize(8);
    }

    // ---------------------------------------------------------------
    // Delete
    // ---------------------------------------------------------------

    @Test
    void delete_anySubtype() {
        itemService.delete(1L);  // TypeA
        itemService.delete(4L);  // TypeB
        assertThat(itemService.findAll()).hasSize(4);
    }

    @Test
    void delete_notFound_throws() {
        assertThatThrownBy(() -> itemService.delete(9999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
