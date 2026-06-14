package com.example.jpademo.service;

import com.example.jpademo.dto.*;
import com.example.jpademo.entity.Child;
import com.example.jpademo.entity.ChildDetail;
import com.example.jpademo.entity.Parent;
import com.example.jpademo.exception.ResourceNotFoundException;
import com.example.jpademo.repository.ChildRepository;
import com.example.jpademo.repository.ParentRepository;
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
        Child child = childRepository.findWithDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found: " + id));
        return toDto(child);
    }

    public List<ChildDto> findByParent(Long parentId) {
        return childRepository.findByParentIdWithDetail(parentId).stream()
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

        if (req.getDetail() != null) {
            child.setDetail(toDetailEntity(req.getDetail()));
        }

        // addChild keeps both sides of the bidirectional relationship in sync
        parent.addChild(child);
        return toDto(childRepository.save(child));
    }

    /**
     * Add or replace the one-to-one ChildDetail.
     * Demonstrates the one-to-one upsert pattern.
     */
    @Transactional
    public ChildDto upsertDetail(Long childId, ChildDetailDto dto) {
        Child child = childRepository.findWithDetailById(childId)
                .orElseThrow(() -> new ResourceNotFoundException("Child not found: " + childId));

        ChildDetail detail = child.getDetail();
        if (detail == null) {
            detail = new ChildDetail();
        }
        detail.setDescription(dto.getDescription());
        detail.setAdditionalInfo(dto.getAdditionalInfo());
        detail.setNumericValue(dto.getNumericValue());
        detail.setNotes(dto.getNotes());

        child.setDetail(detail);
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
        ChildDetailDto detailDto = null;
        if (child.getDetail() != null) {
            ChildDetail d = child.getDetail();
            detailDto = ChildDetailDto.builder()
                    .id(d.getId())
                    .description(d.getDescription())
                    .additionalInfo(d.getAdditionalInfo())
                    .numericValue(d.getNumericValue())
                    .notes(d.getNotes())
                    .build();
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

    private ChildDetail toDetailEntity(ChildDetailDto dto) {
        return ChildDetail.builder()
                .description(dto.getDescription())
                .additionalInfo(dto.getAdditionalInfo())
                .numericValue(dto.getNumericValue())
                .notes(dto.getNotes())
                .build();
    }
}
