package com.example.jpademo.repository;

import com.example.jpademo.entity.Child;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChildRepository extends JpaRepository<Child, Long> {

    // Fetch Child + its one-to-one detail in one query
    @EntityGraph(attributePaths = "detail")
    Optional<Child> findWithDetailById(Long id);

    // All children for a parent, with their details
    @EntityGraph(attributePaths = "detail")
    @Query("SELECT c FROM Child c WHERE c.parent.id = :parentId")
    List<Child> findByParentIdWithDetail(Long parentId);

    List<Child> findByParentId(Long parentId);

    // TODO: add custom finders as needed
}
