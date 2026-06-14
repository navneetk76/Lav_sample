package com.example.inheritance.service;

import com.example.inheritance.dto.CreateItemTypeARequest;
import com.example.inheritance.dto.CreateItemTypeBRequest;
import com.example.inheritance.dto.ItemDto;
import com.example.inheritance.entity.Item;
import com.example.inheritance.entity.ItemTypeA;
import com.example.inheritance.entity.ItemTypeB;
import com.example.inheritance.exception.ResourceNotFoundException;
import com.example.inheritance.repository.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository itemRepository;

    // Returns all items — polymorphic: mix of TypeA and TypeB
    public List<ItemDto> findAll() {
        return itemRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    // Returns only TypeA items
    public List<ItemDto> findAllTypeA() {
        return itemRepository.findAllTypeA().stream()
                .map(this::toDto)
                .toList();
    }

    // Returns only TypeB items
    public List<ItemDto> findAllTypeB() {
        return itemRepository.findAllTypeB().stream()
                .map(this::toDto)
                .toList();
    }

    public ItemDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        return toDto(item);
    }

    @Transactional
    public ItemDto createTypeA(CreateItemTypeARequest req) {
        ItemTypeA item = ItemTypeA.builder()
                .name(req.getName())
                .address(req.getAddress())
                .birthYear(req.getbirthYear())
                .isMale(req.getisMale())
                .studentClass(req.getstudentClass())
                .build();
        return toDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto createTypeB(CreateItemTypeBRequest req) {
        ItemTypeB item = ItemTypeB.builder()
                .name(req.getName())
                .address(req.getAddress())
                .birthYear(req.getbirthYear())
                .isMale(req.getisMale())
                .qualification(req.getqualification())
                .experienceYrs(req.getexperienceYrs())
                .build();
        return toDto(itemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        itemRepository.delete(item);
    }

    // ---- Mapping helper — instanceof determines subtype and itemType label ----

    public ItemDto toDto(Item item) {
        ItemDto.ItemDtoBuilder builder = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .address(item.getaddress())
                .birthYear(item.getbirthYear())
                .isMale(item.getisMale());

        // Derive the itemType string from the concrete class
        if (item instanceof ItemTypeA a) {
            builder.itemType("STUDENT")
                    .studentClass(a.getstudentClass());
        } else if (item instanceof ItemTypeB b) {
            builder.itemType("TEACHER")
                   .qualification(b.getqualification())
                   .experienceYrs(b.getexperienceYrs());
        }

        return builder.build();
    }
}
