package com.example.jpademo.service;

import com.example.jpademo.dto.*;
import com.example.jpademo.entity.Child;
import com.example.jpademo.entity.ChildDetail;
import com.example.jpademo.entity.Parent;
import com.example.jpademo.exception.ResourceNotFoundException;
import com.example.jpademo.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ParentService {

    private final ParentRepository parentRepository;

    public List<ParentDto> findAll() {
        return parentRepository.findAllWithChildren().stream()
                .map(this::toDto)
                .toList();
    }

    public ParentDto findById(Long id) {
        Parent parent = parentRepository.findWithChildrenById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + id));
        return toDto(parent);
    }

    @Transactional
    public ParentDto create(CreateParentRequest req) {
        Parent parent = Parent.builder()
                .name(req.getName())
                .category(req.getCategory())
                .description(req.getDescription())
                .entryYear(req.getEntryYear())
                .build();
        return toDto(parentRepository.save(parent));
    }

    @Transactional
    public ParentDto update(Long id, CreateParentRequest req) {
        Parent parent = parentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + id));
        parent.setName(req.getName());
        parent.setCategory(req.getCategory());
        parent.setDescription(req.getDescription());
        parent.setEntryYear(req.getEntryYear());
        return toDto(parentRepository.save(parent));
    }

    @Transactional
    public void delete(Long id) {
        // Load children eagerly so Hibernate's cascade fires for each child
        Parent parent = parentRepository.findWithChildrenById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Parent not found: " + id));
        parentRepository.delete(parent);
    }

    // ---- Mapping helpers — update when you rename fields ----

    public ParentDto toDto(Parent parent) {
        List<ChildDto> childDtos = parent.getChildren().stream()
                .map(this::toChildDto)
                .toList();
        return ParentDto.builder()
                .id(parent.getId())
                .name(parent.getName())
                .category(parent.getCategory())
                .description(parent.getDescription())
                .entryYear(parent.getEntryYear())
                .children(childDtos)
                .build();
    }

    private ChildDto toChildDto(Child child) {
        ChildDetailDto detailDto = null;
        if (child.getDetail() != null) {
            detailDto = toDetailDto(child.getDetail());
        }
        return ChildDto.builder()
                .id(child.getId())
                .name(child.getName())
                .dataValue(child.getDataValue())
                .entryYear(child.getEntryYear())
                .parentId(child.getParent().getId())
                .parentName(child.getParent().getName())
                .detail(detailDto)
                .build();
    }

    private ChildDetailDto toDetailDto(ChildDetail d) {
        return ChildDetailDto.builder()
                .id(d.getId())
                .description(d.getDescription())
                .additionalInfo(d.getAdditionalInfo())
                .numericValue(d.getNumericValue())
                .notes(d.getNotes())
                .build();
    }
}
