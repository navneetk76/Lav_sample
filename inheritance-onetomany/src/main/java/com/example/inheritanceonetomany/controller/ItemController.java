package com.example.inheritanceonetomany.controller;

import com.example.inheritanceonetomany.dto.CreateItemTypeARequest;
import com.example.inheritanceonetomany.dto.CreateItemTypeBRequest;
import com.example.inheritanceonetomany.dto.ItemDto;
import com.example.inheritanceonetomany.service.ItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Change the base path to match your domain, e.g. /api/vehicles
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    // GET /api/items  — all items across all owners (polymorphic)
    @GetMapping
    public List<ItemDto> getAll() { return itemService.findAll(); }

    // GET /api/items/{id}
    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) { return itemService.findById(id); }

    // GET /api/items?ownerId=1            — all items of an owner (any subtype)
    // GET /api/items?ownerId=1&subtype=TYPE_A  — filter by subtype too
    @GetMapping(params = "ownerId")
    public List<ItemDto> getByOwner(
            @RequestParam Long ownerId,
            @RequestParam(required = false) String subtype) {
        if ("TYPE_A".equalsIgnoreCase(subtype)) return itemService.findTypeAByOwner(ownerId);
        if ("TYPE_B".equalsIgnoreCase(subtype)) return itemService.findTypeBByOwner(ownerId);
        return itemService.findByOwner(ownerId);
    }

    // POST /api/items/type-a
    @PostMapping("/type-a")
    public ResponseEntity<ItemDto> createTypeA(@Valid @RequestBody CreateItemTypeARequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createTypeA(req));
    }

    // POST /api/items/type-b
    @PostMapping("/type-b")
    public ResponseEntity<ItemDto> createTypeB(@Valid @RequestBody CreateItemTypeBRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createTypeB(req));
    }

    // DELETE /api/items/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { itemService.delete(id); }
}
