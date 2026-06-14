package com.example.inheritanceonetomany.controller;

import com.example.inheritanceonetomany.dto.CreateOwnerRequest;
import com.example.inheritanceonetomany.dto.OwnerDto;
import com.example.inheritanceonetomany.service.OwnerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Change the base path to match your domain, e.g. /api/departments
@RestController
@RequestMapping("/api/owners")
@RequiredArgsConstructor
public class OwnerController {

    private final OwnerService ownerService;

    // GET /api/owners  — each owner includes all its items (mixed subtypes)
    @GetMapping
    public List<OwnerDto> getAll() { return ownerService.findAll(); }

    @GetMapping("/{id}")
    public OwnerDto getById(@PathVariable Long id) { return ownerService.findById(id); }

    @PostMapping
    public ResponseEntity<OwnerDto> create(@Valid @RequestBody CreateOwnerRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.create(req));
    }

    @PutMapping("/{id}")
    public OwnerDto update(@PathVariable Long id, @Valid @RequestBody CreateOwnerRequest req) {
        return ownerService.update(id, req);
    }

    // DELETE cascades to all items (any subtype)
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) { ownerService.delete(id); }
}
