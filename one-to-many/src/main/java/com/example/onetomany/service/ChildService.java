package com.example.onetomany.service;

import com.example.onetomany.dto.ChildDto;
import com.example.onetomany.dto.CreateChildRequest;
import com.example.onetomany.entity.Child;
import com.example.onetomany.entity.Parent;
import com.example.onetomany.exception.ResourceNotFoundException;
import com.example.onetomany.repository.ChildRepository;
import com.example.onetomany.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ChildService {

    private final ChildRepository childRepository;
    private final ParentRepository parentRepository;

    public List<ChildDto> findAll() {
        return childRepository.findAll().stream()
                .map(this::toDto)
                .toList();
    }

    public ChildDto findById(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found: " + id));
        return toDto(child);
    }

    public List<ChildDto> findByParent(Long parentId) {
        return childRepository.findByParentId(parentId).stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public ChildDto create(CreateChildRequest req) {
        Parent parent = parentRepository.findById(req.getParentId())
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + req.getParentId()));

        Child child = Child.builder()
                .name(req.getName())
                .dataValue(req.getDataValue())
                .entryYear(req.getEntryYear())
                .build();

        // addChild keeps both sides of the bidirectional relationship in sync
        parent.addChild(child);
        return toDto(childRepository.save(child));
    }

    @Transactional
    public ChildDto update(Long id, CreateChildRequest req) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found: " + id));

        // If moving to a different parent, update the FK side
        if (!child.getParent().getId().equals(req.getParentId())) {
            Parent oldParent = child.getParent();
            Parent newParent = parentRepository.findById(req.getParentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + req.getParentId()));
            oldParent.removeChild(child);
            newParent.addChild(child);
        }

        child.setName(req.getName());
        child.setDataValue(req.getDataValue());
        child.setEntryYear(req.getEntryYear());
        return toDto(childRepository.save(child));
    }

    @Transactional
    public void delete(Long id) {
        Child child = childRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found: " + id));
        childRepository.delete(child);
    }

    // ---- Mapping helpers — update when you rename fields ----

    public ChildDto toDto(Child child) {
        return ChildDto.builder()
                .id(child.getId())
                .name(child.getName())
                .dataValue(child.getDataValue())
                .entryYear(child.getEntryYear())
                .parentId(child.getParent().getId())
                .parentName(child.getParent().getName())
                .build();
    }
}
