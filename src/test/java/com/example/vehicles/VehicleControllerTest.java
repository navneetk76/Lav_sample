package com.example.vehicles;

import com.example.vehicles.exception.VehicleNotFoundException;
import com.example.vehicles.model.Car;
import com.example.vehicles.model.Motorcycle;
import com.example.vehicles.model.Vehicle;
import com.example.vehicles.service.VehicleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Web-layer tests using MockMvc.
 * VehicleService is mocked — no real database or HTTP server needed.
 */
@WebMvcTest
class VehicleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private VehicleService service;

    // ── GET /api/vehicles ────────────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/vehicles returns 200 with vehicle list")
    void getAll_returns200() throws Exception {
        Car car = new Car("Toyota", "Camry", 2022, 25_000.0, 4);
        Motorcycle moto = new Motorcycle("Honda", "Gold Wing", 2023, 28_000.0, true);
        when(service.findAll()).thenReturn(List.of(car, moto));

        mockMvc.perform(get("/api/vehicles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].brand", is("Toyota")))
                .andExpect(jsonPath("$[1].brand", is("Honda")));
    }

    // ── GET /api/vehicles/{id} ───────────────────────────────────────────────

    @Test
    @DisplayName("GET /api/vehicles/{id} returns 200 for known id")
    void getById_found() throws Exception {
        when(service.findById(1L)).thenReturn(new Car("Toyota", "Camry", 2022, 25_000.0, 4));

        mockMvc.perform(get("/api/vehicles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.brand", is("Toyota")));
    }

    @Test
    @DisplayName("GET /api/vehicles/{id} returns 404 for unknown id")
    void getById_notFound() throws Exception {
        when(service.findById(99L)).thenThrow(new VehicleNotFoundException(99L));

        mockMvc.perform(get("/api/vehicles/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error",  is("Not Found")));
    }

    // ── POST /api/vehicles/cars ──────────────────────────────────────────────

    @Test
    @DisplayName("POST /api/vehicles/cars returns 201 with saved car")
    void createCar_returns201() throws Exception {
        Car car = new Car("Ford", "Mustang", 2023, 45_000.0, 2);
        when(service.save(any(Vehicle.class))).thenReturn(car);

        mockMvc.perform(post("/api/vehicles/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(car)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.brand", is("Ford")))
                .andExpect(jsonPath("$.numberOfDoors", is(2)));
    }

    @Test
    @DisplayName("POST /api/vehicles/cars returns 400 when brand is blank")
    void createCar_validationError() throws Exception {
        Car invalidCar = new Car("", "Mustang", 2023, 45_000.0, 2);

        mockMvc.perform(post("/api/vehicles/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidCar)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Validation Failed")))
                .andExpect(jsonPath("$.messages", hasItem(containsString("brand"))));
    }

    // ── DELETE /api/vehicles/{id} ────────────────────────────────────────────

    @Test
    @DisplayName("DELETE /api/vehicles/{id} returns 204 on success")
    void delete_returns204() throws Exception {
        doNothing().when(service).delete(1L);

        mockMvc.perform(delete("/api/vehicles/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/vehicles/{id} returns 404 when not found")
    void delete_notFound() throws Exception {
        doThrow(new VehicleNotFoundException(99L)).when(service).delete(99L);

        mockMvc.perform(delete("/api/vehicles/99"))
                .andExpect(status().isNotFound());
    }

    // ── GET /api/vehicles/price-range ────────────────────────────────────────

    @Test
    @DisplayName("GET /api/vehicles/price-range returns 400 when min > max")
    void priceRange_invalidRange() throws Exception {
        mockMvc.perform(get("/api/vehicles/price-range")
                        .param("min", "50000")
                        .param("max", "1000"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Bad Request")));
    }

    // ── GET /api/vehicles/describe ───────────────────────────────────────────

    @Test
    @DisplayName("GET /api/vehicles/describe returns polymorphic descriptions")
    void describe_returnsDescriptions() throws Exception {
        when(service.describeAll()).thenReturn(List.of(
                "2022 Toyota Camry — Car with 4 doors, priced at $25000.00",
                "2023 Honda Gold Wing — Motorcycle (with sidecar), priced at $28000.00"
        ));

        mockMvc.perform(get("/api/vehicles/describe"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]", containsString("Car")))
                .andExpect(jsonPath("$[1]", containsString("sidecar")));
    }
}
