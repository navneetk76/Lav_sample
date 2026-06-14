package com.example.onetomany.controller;

import com.example.onetomany.dto.ChildDto;
import com.example.onetomany.dto.CreateChildRequest;
import com.example.onetomany.service.ChildService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: Change the base path to match your domain, e.g. /api/employees
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

    // GET /api/children/{id}
    @GetMapping("/{id}")
    public ChildDto getById(@PathVariable Long id) {
        return childService.findById(id);
    }

    // GET /api/children?parentId=1  — all children belonging to a parent
    @GetMapping(params = "parentId")
    public List<ChildDto> getByParent(@RequestParam Long parentId) {
        return childService.findByParent(parentId);
    }

    // POST /api/children
    @PostMapping
    public ResponseEntity<ChildDto> create(@Valid @RequestBody CreateChildRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(childService.create(req));
    }

    // PUT /api/children/{id}  — also handles re-parenting (moving to a different parent)
    @PutMapping("/{id}")
    public ChildDto update(@PathVariable Long id, @Valid @RequestBody CreateChildRequest req) {
        return childService.update(id, req);
    }

    // DELETE /api/children/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        childService.delete(id);
    }
}
