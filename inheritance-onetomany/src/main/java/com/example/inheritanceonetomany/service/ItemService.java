package com.example.inheritanceonetomany.service;

import com.example.inheritanceonetomany.dto.CreateItemTypeARequest;
import com.example.inheritanceonetomany.dto.CreateItemTypeBRequest;
import com.example.inheritanceonetomany.dto.ItemDto;
import com.example.inheritanceonetomany.entity.Item;
import com.example.inheritanceonetomany.entity.ItemTypeA;
import com.example.inheritanceonetomany.entity.ItemTypeB;
import com.example.inheritanceonetomany.entity.Owner;
import com.example.inheritanceonetomany.exception.ResourceNotFoundException;
import com.example.inheritanceonetomany.repository.ItemRepository;
import com.example.inheritanceonetomany.repository.OwnerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {

    private final ItemRepository  itemRepository;
    private final OwnerRepository ownerRepository;
    private final OwnerService    ownerService;   // reuse toItemDto mapping

    public List<ItemDto> findAll() {
        return itemRepository.findAll().stream().map(ownerService::toItemDto).toList();
    }

    public ItemDto findById(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        return ownerService.toItemDto(item);
    }

    // All items (any subtype) belonging to one owner
    public List<ItemDto> findByOwner(Long ownerId) {
        return itemRepository.findByOwnerId(ownerId).stream()
                .map(ownerService::toItemDto).toList();
    }

    // Only TypeA items for one owner
    public List<ItemDto> findTypeAByOwner(Long ownerId) {
        return itemRepository.findTypeAByOwnerId(ownerId).stream()
                .map(ownerService::toItemDto).toList();
    }

    // Only TypeB items for one owner
    public List<ItemDto> findTypeBByOwner(Long ownerId) {
        return itemRepository.findTypeBByOwnerId(ownerId).stream()
                .map(ownerService::toItemDto).toList();
    }

    @Transactional
    public ItemDto createTypeA(CreateItemTypeARequest req) {
        Owner owner = findOwner(req.getOwnerId());
        ItemTypeA item = ItemTypeA.builder()
                .name(req.getName())
                .description(req.getDescription())
                .entryYear(req.getEntryYear())
                .extraFieldA(req.getExtraFieldA())
                .build();
        owner.addItem(item);   // keeps both sides in sync
        return ownerService.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public ItemDto createTypeB(CreateItemTypeBRequest req) {
        Owner owner = findOwner(req.getOwnerId());
        ItemTypeB item = ItemTypeB.builder()
                .name(req.getName())
                .description(req.getDescription())
                .entryYear(req.getEntryYear())
                .extraFieldB(req.getExtraFieldB())
                .numericField(req.getNumericField())
                .build();
        owner.addItem(item);
        return ownerService.toItemDto(itemRepository.save(item));
    }

    @Transactional
    public void delete(Long id) {
        Item item = itemRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found: " + id));
        itemRepository.delete(item);
    }

    private Owner findOwner(Long ownerId) {
        return ownerRepository.findById(ownerId)
                .orElseThrow(() -> new ResourceNotFoundException("Owner not found: " + ownerId));
    }
}
