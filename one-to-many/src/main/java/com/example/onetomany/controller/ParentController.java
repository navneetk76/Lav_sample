package com.example.onetomany.controller;

import com.example.onetomany.dto.CreateParentRequest;
import com.example.onetomany.dto.ParentDto;
import com.example.onetomany.service.ParentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Change the base path to match your domain, e.g. /api/departments
@RestController
@RequestMapping("/api/parents")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    // GET /api/parents  — each parent includes its full list of children
    @GetMapping
    public List<ParentDto> getAll() {
        return parentService.findAll();
    }

    // GET /api/parents/{id}
    @GetMapping("/{id}")
    public ParentDto getById(@PathVariable Long id) {
        return parentService.findById(id);
    }

    // POST /api/parents
    @PostMapping
    public ResponseEntity<ParentDto> create(@Valid @RequestBody CreateParentRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(parentService.create(req));
    }

    // PUT /api/parents/{id}
    @PutMapping("/{id}")
    public ParentDto update(@PathVariable Long id, @Valid @RequestBody CreateParentRequest req) {
        return parentService.update(id, req);
    }

    // DELETE /api/parents/{id}  — cascades to all children
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        parentService.delete(id);
    }
}
