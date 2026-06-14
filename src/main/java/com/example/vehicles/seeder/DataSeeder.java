package com.example.vehicles.seeder;

import com.example.vehicles.model.Car;
import com.example.vehicles.model.Motorcycle;
import com.example.vehicles.repository.VehicleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Seeds the database with sample vehicles on startup.
 * Skipped during tests (profile "test") so each test class controls its own data.
 */
@Component
@Profile("!test")
public class DataSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    private final VehicleRepository repository;

    public DataSeeder(VehicleRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            log.info("Database already seeded — skipping.");
            return;
        }

        log.info("Seeding database with sample vehicles...");

        repository.save(new Car("Toyota",    "Camry",      2022, 25_000.0, 4));
        repository.save(new Car("Honda",     "Civic",      2021, 22_000.0, 4));
        repository.save(new Car("Ford",      "Mustang",    2023, 45_000.0, 2));
        repository.save(new Car("Tesla",     "Model 3",    2023, 42_000.0, 4));
        repository.save(new Motorcycle("Harley-Davidson", "Sportster",    2022, 15_000.0, false));
        repository.save(new Motorcycle("Honda",           "Gold Wing",    2023, 28_000.0, true));
        repository.save(new Motorcycle("Yamaha",          "MT-07",        2021, 8_000.0,  false));

        log.info("Seeded {} vehicles.", repository.count());
    }
}
