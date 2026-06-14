package com.example.onetomany.repository;

import com.example.onetomany.entity.Child;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChildRepository extends JpaRepository<Child, Long> {

    // All children belonging to a given parent
    List<Child> findByParentId(Long parentId);

    // TODO: add custom finders as needed
}
