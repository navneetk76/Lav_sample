package com.example.vehicles.controller;

import com.example.vehicles.model.Car;
import com.example.vehicles.model.Motorcycle;
import com.example.vehicles.model.Vehicle;
import com.example.vehicles.service.VehicleService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller — all endpoints live under /api/vehicles.
 *
 * POST /api/vehicles/cars        create a car
 * POST /api/vehicles/motorcycles create a motorcycle
 * GET  /api/vehicles             list all vehicles
 * GET  /api/vehicles/{id}        get one vehicle
 * GET  /api/vehicles/brand/{b}   filter by brand
 * GET  /api/vehicles/price-range filter by price range
 * GET  /api/vehicles/describe    polymorphic descriptions
 * PATCH /api/vehicles/{id}       partial update
 * DELETE /api/vehicles/{id}      delete
 */
@RestController
@RequestMapping("/api/vehicles")
public class VehicleController {

    private final VehicleService service;

    public VehicleController(VehicleService service) {
        this.service = service;
    }

    // ── Create ───────────────────────────────────────────────────────────────

    @PostMapping("/cars")
    public ResponseEntity<Vehicle> createCar(@Valid @RequestBody Car car) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(car));
    }

    @PostMapping("/motorcycles")
    public ResponseEntity<Vehicle> createMotorcycle(@Valid @RequestBody Motorcycle motorcycle) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(motorcycle));
    }

    // ── Read ─────────────────────────────────────────────────────────────────

    @GetMapping
    public List<Vehicle> getAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public Vehicle getById(@PathVariable Long id) {
        return service.findById(id);
    }

    @GetMapping("/brand/{brand}")
    public List<Vehicle> getByBrand(@PathVariable String brand) {
        return service.findByBrand(brand);
    }

    @GetMapping("/price-range")
    public List<Vehicle> getByPriceRange(@RequestParam Double min, @RequestParam Double max) {
        if (min > max) {
            throw new IllegalArgumentException("min price must be less than or equal to max price");
        }
        return service.findByPriceRange(min, max);
    }

    @GetMapping("/describe")
    public List<String> describeAll() {
        return service.describeAll();
    }

    // ── Update ───────────────────────────────────────────────────────────────

    @PatchMapping("/{id}")
    public Vehicle partialUpdate(@PathVariable Long id, @RequestBody Map<String, Object> fields) {
        return service.partialUpdate(id, fields);
    }

    // ── Delete ───────────────────────────────────────────────────────────────

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
