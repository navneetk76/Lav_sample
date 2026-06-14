package com.example.inheritance.controller;

import com.example.inheritance.dto.CreateItemTypeARequest;
import com.example.inheritance.dto.CreateItemTypeBRequest;
import com.example.inheritance.dto.ItemDto;
import com.example.inheritance.service.ItemService;
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

    // GET /api/items  — returns ALL items (mixed TypeA + TypeB)
    @GetMapping
    public List<ItemDto> getAll() {
        return itemService.findAll();
    }

    // GET /api/items/{id}
    @GetMapping("/{id}")
    public ItemDto getById(@PathVariable Long id) {
        return itemService.findById(id);
    }

    // GET /api/items?subtype=TYPE_A  — filter by discriminator value
    @GetMapping(params = "subtype")
    public List<ItemDto> getBySubtype(@RequestParam String subtype) {
        return switch (subtype.toUpperCase()) {
            case "TYPE_A" -> itemService.findAllTypeA();
            case "TYPE_B" -> itemService.findAllTypeB();
            default       -> itemService.findAll();
        };
    }

    // POST /api/items/type-a  — create a TypeA instance
    @PostMapping("/type-a")
    public ResponseEntity<ItemDto> createTypeA(@Valid @RequestBody CreateItemTypeARequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createTypeA(req));
    }

    // POST /api/items/type-b  — create a TypeB instance
    @PostMapping("/type-b")
    public ResponseEntity<ItemDto> createTypeB(@Valid @RequestBody CreateItemTypeBRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(itemService.createTypeB(req));
    }

    // DELETE /api/items/{id}  — works for any subtype
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        itemService.delete(id);
    }
}
