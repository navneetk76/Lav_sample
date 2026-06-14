package com.example.vehicles.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Abstract base entity.  All shared columns live here;
 * subclass-specific columns go in Car / Motorcycle.
 *
 * SINGLE_TABLE strategy stores every subtype in one table,
 * using a discriminator column ("vehicle_type") to tell them apart.
 */
@Entity
@Table(name = "vehicles")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "vehicle_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Vehicle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Brand is required")
    private String brand;

    @NotBlank(message = "Model is required")
    private String model;

    @NotNull(message = "Year is required")
    @Min(value = 1886, message = "Year must be 1886 or later")
    private Integer year;

    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    private Double price;

    // Discriminator value is available as a convenience read-only field
    @Column(name = "vehicle_type", insertable = false, updatable = false)
    private String vehicleType;

    protected Vehicle() {}

    protected Vehicle(String brand, String model, Integer year, Double price) {
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.price = price;
    }

    /** Returns a human-readable description — each subclass overrides this. */
    public abstract String describe();

    // ── Getters / Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public String getVehicleType() { return vehicleType; }
}
