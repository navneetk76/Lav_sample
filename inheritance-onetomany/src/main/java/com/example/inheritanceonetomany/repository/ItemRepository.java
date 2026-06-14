package com.example.inheritanceonetomany.repository;

import com.example.inheritanceonetomany.entity.Item;
import com.example.inheritanceonetomany.entity.ItemTypeA;
import com.example.inheritanceonetomany.entity.ItemTypeB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // One-to-many: all items (any subtype) belonging to an owner
    List<Item> findByOwnerId(Long ownerId);

    // Inheritance: query for a specific subtype only
    @Query("SELECT a FROM ItemTypeA a WHERE a.owner.id = :ownerId")
    List<ItemTypeA> findTypeAByOwnerId(Long ownerId);

    @Query("SELECT b FROM ItemTypeB b WHERE b.owner.id = :ownerId")
    List<ItemTypeB> findTypeBByOwnerId(Long ownerId);

    // All TypeA or TypeB across all owners
    @Query("SELECT a FROM ItemTypeA a")
    List<ItemTypeA> findAllTypeA();

    @Query("SELECT b FROM ItemTypeB b")
    List<ItemTypeB> findAllTypeB();
}
