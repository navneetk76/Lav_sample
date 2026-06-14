package com.example.vehicles;

import com.example.vehicles.exception.VehicleNotFoundException;
import com.example.vehicles.model.Car;
import com.example.vehicles.model.Motorcycle;
import com.example.vehicles.model.Vehicle;
import com.example.vehicles.repository.VehicleRepository;
import com.example.vehicles.service.VehicleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Pure unit tests for VehicleService.
 * VehicleRepository is mocked — no database involved.
 */
@ExtendWith(MockitoExtension.class)
class VehicleServiceTest {

    @Mock
    private VehicleRepository repository;

    @InjectMocks
    private VehicleService service;

    private Car sampleCar;
    private Motorcycle sampleMoto;

    @BeforeEach
    void setUp() {
        sampleCar  = new Car("Toyota", "Camry", 2022, 25_000.0, 4);
        sampleMoto = new Motorcycle("Honda", "Gold Wing", 2023, 28_000.0, true);
    }

    // ── findAll ──────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findAll returns every vehicle from repository")
    void findAll_returnsAllVehicles() {
        when(repository.findAll()).thenReturn(List.of(sampleCar, sampleMoto));

        List<Vehicle> result = service.findAll();

        assertThat(result).hasSize(2);
        verify(repository, times(1)).findAll();
    }

    // ── findById ─────────────────────────────────────────────────────────────

    @Test
    @DisplayName("findById returns vehicle when id exists")
    void findById_found() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleCar));

        Vehicle result = service.findById(1L);

        assertThat(result).isInstanceOf(Car.class);
        assertThat(result.getBrand()).isEqualTo("Toyota");
    }

    @Test
    @DisplayName("findById throws VehicleNotFoundException when id absent")
    void findById_notFound() {
        when(repository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(99L))
                .isInstanceOf(VehicleNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ── save ─────────────────────────────────────────────────────────────────

    @Test
    @DisplayName("save persists and returns the vehicle")
    void save_persistsVehicle() {
        when(repository.save(any(Vehicle.class))).thenReturn(sampleCar);

        Vehicle saved = service.save(sampleCar);

        assertThat(saved.getBrand()).isEqualTo("Toyota");
        verify(repository).save(sampleCar);
    }

    // ── partialUpdate ────────────────────────────────────────────────────────

    @Test
    @DisplayName("partialUpdate changes only supplied fields")
    void partialUpdate_updatesFields() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleCar));
        when(repository.save(any(Car.class))).thenAnswer(inv -> inv.getArgument(0));

        Vehicle updated = service.partialUpdate(1L, Map.of("price", 30_000, "numberOfDoors", 2));

        assertThat(updated.getPrice()).isEqualTo(30_000.0);
        assertThat(((Car) updated).getNumberOfDoors()).isEqualTo(2);
        assertThat(updated.getBrand()).isEqualTo("Toyota"); // unchanged
    }

    // ── delete ───────────────────────────────────────────────────────────────

    @Test
    @DisplayName("delete removes existing vehicle")
    void delete_existingVehicle() {
        when(repository.existsById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    @DisplayName("delete throws VehicleNotFoundException for missing id")
    void delete_notFound() {
        when(repository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(99L))
                .isInstanceOf(VehicleNotFoundException.class);
    }

    // ── describeAll (polymorphism) ────────────────────────────────────────────

    @Test
    @DisplayName("describeAll delegates to each subtype's describe() override")
    void describeAll_usesPolymorphicDescribe() {
        when(repository.findAll()).thenReturn(List.of(sampleCar, sampleMoto));

        List<String> descriptions = service.describeAll();

        assertThat(descriptions).hasSize(2);
        assertThat(descriptions.get(0)).contains("Car");
        assertThat(descriptions.get(1)).contains("Motorcycle").contains("sidecar");
    }

    // ── Inheritance behaviour ────────────────────────────────────────────────

    @Test
    @DisplayName("Car IS-A Vehicle (inheritance)")
    void car_isAVehicle() {
        assertThat(sampleCar).isInstanceOf(Vehicle.class);
        assertThat(sampleCar.describe()).contains("Car with 4 doors");
    }

    @Test
    @DisplayName("Motorcycle IS-A Vehicle (inheritance)")
    void motorcycle_isAVehicle() {
        assertThat(sampleMoto).isInstanceOf(Vehicle.class);
        assertThat(sampleMoto.describe()).contains("Motorcycle").contains("sidecar");
    }
}
