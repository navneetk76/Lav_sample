package com.example.vehicles.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

/** A two-wheeled vehicle.  Adds an optional sidecar flag. */
@Entity
@DiscriminatorValue("MOTORCYCLE")
public class Motorcycle extends Vehicle {

    private boolean hasSidecar;

    public Motorcycle() {}

    public Motorcycle(String brand, String model, Integer year, Double price, boolean hasSidecar) {
        super(brand, model, year, price);
        this.hasSidecar = hasSidecar;
    }

    @Override
    public String describe() {
        String sidecar = hasSidecar ? " (with sidecar)" : "";
        return String.format("%d %s %s — Motorcycle%s, priced at $%.2f",
                getYear(), getBrand(), getModel(), sidecar, getPrice());
    }

    public boolean isHasSidecar() { return hasSidecar; }
    public void setHasSidecar(boolean hasSidecar) { this.hasSidecar = hasSidecar; }
}
