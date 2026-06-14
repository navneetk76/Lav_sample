package com.example.inheritance.repository;

import com.example.inheritance.entity.Item;
import com.example.inheritance.entity.ItemTypeA;
import com.example.inheritance.entity.ItemTypeB;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Spring Data JPA automatically adds WHERE item_type = 'TYPE_A'
    // when the return type is a subclass.
    @Query("SELECT a FROM ItemTypeA a")
    List<ItemTypeA> findAllTypeA();

    @Query("SELECT b FROM ItemTypeB b")
    List<ItemTypeB> findAllTypeB();

    // TODO: add custom finders as needed
}
