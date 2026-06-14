package com.example.vehicles.repository;

import com.example.vehicles.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    List<Vehicle> findByBrandIgnoreCase(String brand);

    List<Vehicle> findByYearGreaterThanEqual(Integer year);

    @Query("SELECT v FROM Vehicle v WHERE v.price BETWEEN :min AND :max")
    List<Vehicle> findByPriceRange(@Param("min") Double min, @Param("max") Double max);
}
