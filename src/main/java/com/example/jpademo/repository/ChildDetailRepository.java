package com.example.jpademo.repository;

import com.example.jpademo.entity.ChildDetail;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChildDetailRepository extends JpaRepository<ChildDetail, Long> {

    Optional<ChildDetail> findByChildId(Long childId);
}
