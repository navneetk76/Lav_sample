package com.example.vehicles.service;

import com.example.vehicles.exception.VehicleNotFoundException;
import com.example.vehicles.model.Car;
import com.example.vehicles.model.Motorcycle;
import com.example.vehicles.model.Vehicle;
import com.example.vehicles.repository.VehicleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Transactional
public class VehicleService {

    private final VehicleRepository repository;

    public VehicleService(VehicleRepository repository) {
        this.repository = repository;
    }

    // ── Read operations ──────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Vehicle> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Vehicle findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByBrand(String brand) {
        return repository.findByBrandIgnoreCase(brand);
    }

    @Transactional(readOnly = true)
    public List<Vehicle> findByPriceRange(Double min, Double max) {
        return repository.findByPriceRange(min, max);
    }

    // ── Write operations ─────────────────────────────────────────────────────

    public Vehicle save(Vehicle vehicle) {
        return repository.save(vehicle);
    }

    /**
     * Partial update using a map of fields.
     * Only the keys present in the map are applied — absent keys are left unchanged.
     */
    public Vehicle partialUpdate(Long id, Map<String, Object> fields) {
        Vehicle vehicle = findById(id);

        if (fields.containsKey("brand"))  vehicle.setBrand((String) fields.get("brand"));
        if (fields.containsKey("model"))  vehicle.setModel((String) fields.get("model"));
        if (fields.containsKey("year"))   vehicle.setYear(((Number) fields.get("year")).intValue());
        if (fields.containsKey("price"))  vehicle.setPrice(((Number) fields.get("price")).doubleValue());

        if (vehicle instanceof Car car && fields.containsKey("numberOfDoors")) {
            car.setNumberOfDoors(((Number) fields.get("numberOfDoors")).intValue());
        }
        if (vehicle instanceof Motorcycle moto && fields.containsKey("hasSidecar")) {
            moto.setHasSidecar((Boolean) fields.get("hasSidecar"));
        }

        return repository.save(vehicle);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new VehicleNotFoundException(id);
        }
        repository.deleteById(id);
    }

    /** Returns each vehicle's human-readable description (uses the overridden describe() method). */
    @Transactional(readOnly = true)
    public List<String> describeAll() {
        return repository.findAll().stream()
                .map(Vehicle::describe)
                .toList();
    }
}
