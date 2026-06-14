package com.example.jpademo.repository;

import com.example.jpademo.entity.Parent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {

    // Fetch Parent + children in one query — avoids N+1
    @EntityGraph(attributePaths = "children")
    Optional<Parent> findWithChildrenById(Long id);

    // Fetch all parents with their children
    @EntityGraph(attributePaths = "children")
    @Query("SELECT DISTINCT p FROM Parent p")
    List<Parent> findAllWithChildren();

    // TODO: add custom finders as needed, e.g. findByCategory(String category)
    List<Parent> findByCategoryIgnoreCase(String category);
}
