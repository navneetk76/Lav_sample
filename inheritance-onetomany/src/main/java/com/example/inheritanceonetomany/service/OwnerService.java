package com.example.inheritanceonetomany.service;

import com.example.inheritanceonetomany.dto.CreateOwnerRequest;
import com.example.inheritanceonetomany.dto.ItemDto;
import com.example.inheritanceonetomany.dto.OwnerDto;
import com.example.inheritanceonetomany.entity.Item;
import com.example.inheritanceonetomany.entity.ItemTypeA;
import com.example.inheritanceonetomany.entity.ItemTypeB;
import com.example.inheritanceonetomany.entity.Owner;
import com.example.inheritanceonetomany.exception.ResourceNotFoundException;
import com.example.inheritanceonetomany.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OwnerService {

    private final OwnerRepository ownerRepository;

    public List<OwnerDto> findAll() {
        return ownerRepository.findAllWithItems().stream().map(this::toDto).toList();
    }

    public OwnerDto findById(Long id) {
        Owner owner = ownerRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + id));
        return toDto(owner);
    }

    @Transactional
    public OwnerDto create(CreateOwnerRequest req) {
        Owner owner = Owner.builder()
                .name(req.getName())
                .category(req.getCategory())
                .entryYear(req.getEntryYear())
                .build();
        return toDto(ownerRepository.save(owner));
    }

    @Transactional
    public OwnerDto update(Long id, CreateOwnerRequest req) {
        Owner owner = ownerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + id));
        owner.setName(req.getName());
        owner.setCategory(req.getCategory());
        owner.setEntryYear(req.getEntryYear());
        return toDto(ownerRepository.save(owner));
    }

    @Transactional
    public void delete(Long id) {
        Owner owner = ownerRepository.findWithItemsById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + id));
        ownerRepository.delete(owner);
    }

    // ---- Mapping helpers ----

    public OwnerDto toDto(Owner owner) {
        List<ItemDto> itemDtos = owner.getItems().stream().map(this::toItemDto).toList();
        return OwnerDto.builder()
                .id(owner.getId())
                .name(owner.getName())
                .category(owner.getCategory())
                .entryYear(owner.getEntryYear())
                .items(itemDtos)
                .build();
    }

    ItemDto toItemDto(Item item) {
        ItemDto.ItemDtoBuilder b = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .entryYear(item.getEntryYear())
                .ownerId(item.getOwner().getId())
                .ownerName(item.getOwner().getName());

        if (item instanceof ItemTypeA a) {
            b.itemType("TYPE_A").extraFieldA(a.getExtraFieldA());
        } else if (item instanceof ItemTypeB bItem) {
            b.itemType("TYPE_B").extraFieldB(bItem.getExtraFieldB()).numericField(bItem.getNumericField());
        }
        return b.build();
    }
}
