package com.example.inheritanceonetomany.repository;

import com.example.inheritanceonetomany.entity.Owner;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface OwnerRepository extends JpaRepository<Owner, Long> {

    // Fetch owner + all items (any subtype) in one query — avoids N+1
    @EntityGraph(attributePaths = "items")
    Optional<Owner> findWithItemsById(Long id);

    @EntityGraph(attributePaths = "items")
    @Query("SELECT DISTINCT o FROM Owner o")
    List<Owner> findAllWithItems();
}
