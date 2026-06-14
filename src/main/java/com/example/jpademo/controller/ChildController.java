package com.example.jpademo.controller;

import com.example.jpademo.dto.ChildDetailDto;
import com.example.jpademo.dto.ChildDto;
import com.example.jpademo.dto.CreateChildRequest;
import com.example.jpademo.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Change the base path to match your domain, e.g. /api/songs
@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
public class ChildController {

    private final ChildService childService;

    // GET /api/children
    @GetMapping
    public List<ChildDto> getAll() {
        return childService.findAll();
    }

    // GET /api/children/{id}  — includes the one-to-one detail
    @GetMapping("/{id}")
    public ChildDto getById(@PathVariable Long id) {
        return childService.findById(id);
    }

    // GET /api/children?parentId=1  — all children of a parent (one-to-many query)
    @GetMapping(params = "parentId")
    public List<ChildDto> getByParent(@RequestParam Long parentId) {
        return childService.findByParent(parentId);
    }

    // POST /api/children
    @PostMapping
    public ResponseEntity<ChildDto> create(@Valid @RequestBody CreateChildRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(childService.create(req));
    }

    // PUT /api/children/{id}/detail  — add or replace the one-to-one detail
    @PutMapping("/{id}/detail")
    public ChildDto upsertDetail(@PathVariable Long id, @RequestBody ChildDetailDto dto) {
        return childService.upsertDetail(id, dto);
    }

    // DELETE /api/children/{id}  — cascades to its detail
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        childService.delete(id);
    }
}
