package com.example.vehicles.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

/** A four-wheeled vehicle.  Adds door count on top of the base Vehicle. */
@Entity
@DiscriminatorValue("CAR")
public class Car extends Vehicle {

    @NotNull(message = "Number of doors is required")
    @Min(value = 2, message = "A car must have at least 2 doors")
    private Integer numberOfDoors;

    public Car() {}

    public Car(String brand, String model, Integer year, Double price, Integer numberOfDoors) {
        super(brand, model, year, price);
        this.numberOfDoors = numberOfDoors;
    }

    @Override
    public String describe() {
        return String.format("%d %s %s — Car with %d doors, priced at $%.2f",
                getYear(), getBrand(), getModel(), numberOfDoors, getPrice());
    }

    public Integer getNumberOfDoors() { return numberOfDoors; }
    public void setNumberOfDoors(Integer numberOfDoors) { this.numberOfDoors = numberOfDoors; }
}
