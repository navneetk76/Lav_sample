# Vehicle Management API — Spring Boot Sample

A self-contained Spring Boot application that demonstrates five core back-end concepts through a **Vehicle Management** domain.

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Tech Stack](#2-tech-stack)
3. [Project Structure](#3-project-structure)
4. [Concepts Demonstrated](#4-concepts-demonstrated)
   - 4.1 Inheritance
   - 4.2 REST API
   - 4.3 Data Seeding
   - 4.4 Unit Testing
   - 4.5 Error Handling
5. [Running the Application](#5-running-the-application)
6. [API Reference](#6-api-reference)
7. [Sample Requests](#7-sample-requests)

---

## 1. Project Overview

The application manages a fleet of vehicles.  
Two concrete types exist:

| Type | Extra field |
|------|-------------|
| `Car` | `numberOfDoors` |
| `Motorcycle` | `hasSidecar` |

Both share a common `Vehicle` base (brand, model, year, price).

---

## 2. Tech Stack

| Concern | Technology |
|---------|-----------|
| Framework | Spring Boot 3.2 |
| Language | Java 17 |
| Persistence | Spring Data JPA + H2 (in-memory) |
| Validation | Jakarta Bean Validation (`@Valid`) |
| Testing | JUnit 5 + Mockito + MockMvc |
| Build | Maven |

---

## 3. Project Structure

```
src/
├── main/java/com/example/vehicles/
│   ├── VehicleApplication.java         ← Spring Boot entry point
│   ├── model/
│   │   ├── Vehicle.java                ← Abstract JPA base entity
│   │   ├── Car.java                    ← Subclass (CAR discriminator)
│   │   └── Motorcycle.java             ← Subclass (MOTORCYCLE discriminator)
│   ├── repository/
│   │   └── VehicleRepository.java      ← JPA repository with custom queries
│   ├── service/
│   │   └── VehicleService.java         ← Business logic + transaction management
│   ├── controller/
│   │   └── VehicleController.java      ← REST endpoints
│   ├── exception/
│   │   ├── VehicleNotFoundException.java
│   │   ├── ErrorResponse.java          ← Uniform JSON error body
│   │   └── GlobalExceptionHandler.java ← @RestControllerAdvice
│   └── seeder/
│       └── DataSeeder.java             ← CommandLineRunner (skipped in tests)
└── test/java/com/example/vehicles/
    ├── VehicleServiceTest.java         ← Unit tests (Mockito)
    └── VehicleControllerTest.java      ← Web-layer tests (MockMvc)
```

---

## 4. Concepts Demonstrated

### 4.1 Inheritance

**Files:** `model/Vehicle.java`, `model/Car.java`, `model/Motorcycle.java`

Java inheritance is mapped to the database using JPA's **Single Table** strategy:

```
vehicles table
──────────────────────────────────────────────────────────────────
id | vehicle_type | brand | model | year | price | numberOfDoors | hasSidecar
──────────────────────────────────────────────────────────────────
1  | CAR          | Toyota | Camry | 2022 | 25000 | 4             | NULL
2  | MOTORCYCLE   | Honda  | Gold Wing | 2023 | 28000 | NULL      | true
```

Key annotations:

| Annotation | Purpose |
|-----------|---------|
| `@Inheritance(strategy = SINGLE_TABLE)` | One table for all subtypes |
| `@DiscriminatorColumn(name = "vehicle_type")` | Column that identifies the subtype |
| `@DiscriminatorValue("CAR")` | Value written for `Car` rows |
| `@DiscriminatorValue("MOTORCYCLE")` | Value written for `Motorcycle` rows |

`Vehicle` declares an **abstract** `describe()` method.  Each subclass overrides it — this is **runtime polymorphism**:

```java
// Car.describe()
"2022 Toyota Camry — Car with 4 doors, priced at $25000.00"

// Motorcycle.describe()
"2023 Honda Gold Wing — Motorcycle (with sidecar), priced at $28000.00"
```

---

### 4.2 REST API

**File:** `controller/VehicleController.java`

Follows REST conventions:

| Method | Path | Action | Status |
|--------|------|--------|--------|
| POST | `/api/vehicles/cars` | Create a car | 201 |
| POST | `/api/vehicles/motorcycles` | Create a motorcycle | 201 |
| GET | `/api/vehicles` | List all vehicles | 200 |
| GET | `/api/vehicles/{id}` | Get one vehicle | 200 / 404 |
| GET | `/api/vehicles/brand/{brand}` | Filter by brand | 200 |
| GET | `/api/vehicles/price-range?min=&max=` | Filter by price | 200 / 400 |
| GET | `/api/vehicles/describe` | Polymorphic descriptions | 200 |
| PATCH | `/api/vehicles/{id}` | Partial update | 200 / 404 |
| DELETE | `/api/vehicles/{id}` | Delete | 204 / 404 |

The `@Valid` annotation on request bodies triggers automatic validation before the method body runs.

---

### 4.3 Data Seeding

**File:** `seeder/DataSeeder.java`

`DataSeeder` implements `CommandLineRunner`, which Spring Boot calls automatically on startup:

```
Spring Boot starts
      │
      └──► CommandLineRunner.run()
                │
                ├── Count rows in DB
                ├── If 0 → insert 7 sample vehicles (4 cars + 3 motorcycles)
                └── If > 0 → skip (idempotent)
```

The `@Profile("!test")` annotation ensures the seeder is **not registered** during test runs — tests manage their own data to stay isolated.

---

### 4.4 Unit Testing

**Files:** `VehicleServiceTest.java`, `VehicleControllerTest.java`

#### Service tests (`VehicleServiceTest`)

Uses **Mockito** — no Spring context, no database:

```
Test
 │
 ├── Mock VehicleRepository
 ├── Create VehicleService with mocked repo (@InjectMocks)
 └── Call service method → verify result / exception
```

What is tested:
- `findAll` delegates to repository
- `findById` returns the vehicle or throws `VehicleNotFoundException`
- `save` calls `repository.save`
- `partialUpdate` applies only the supplied fields
- `delete` calls `repository.deleteById` or throws if absent
- `describeAll` uses polymorphic `describe()` correctly
- Inheritance: `Car` and `Motorcycle` are both `instanceof Vehicle`

#### Controller tests (`VehicleControllerTest`)

Uses **`@WebMvcTest`** + **MockMvc** — spins up the web layer only, service is mocked:

```
Test
 │
 ├── MockMvc sends HTTP request
 ├── Controller processes it (real controller code runs)
 ├── Service call is intercepted by @MockBean
 └── Assert HTTP status + JSON body
```

What is tested:
- `GET /api/vehicles` returns 200 + array
- `GET /api/vehicles/{id}` returns 200 or 404
- `POST /api/vehicles/cars` returns 201 or 400 (validation)
- `DELETE /api/vehicles/{id}` returns 204 or 404
- `GET /api/vehicles/price-range` returns 400 when min > max
- `GET /api/vehicles/describe` returns polymorphic strings

---

### 4.5 Error Handling

**Files:** `exception/GlobalExceptionHandler.java`, `exception/ErrorResponse.java`

`@RestControllerAdvice` intercepts exceptions thrown anywhere in the controller layer and converts them to a consistent JSON response — no try/catch needed in controllers.

```
Controller throws Exception
         │
         ▼
 GlobalExceptionHandler
         │
         ├── VehicleNotFoundException    → 404 Not Found
         ├── MethodArgumentNotValidException → 400 Validation Failed
         ├── IllegalArgumentException    → 400 Bad Request
         └── Exception (catch-all)       → 500 Internal Server Error
```

Error response shape:

```json
{
  "status": 404,
  "error": "Not Found",
  "messages": ["Vehicle not found with id: 99"],
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 5. Running the Application

### Prerequisites
- Java 17+
- Maven 3.8+

### Start the server

```bash
./mvnw spring-boot:run
```

The server starts on **http://localhost:8080**.  
The H2 console is at **http://localhost:8080/h2-console** (JDBC URL: `jdbc:h2:mem:vehiclesdb`).

### Run the tests

```bash
./mvnw test
```

---

## 6. API Reference

### Create a Car

```
POST /api/vehicles/cars
Content-Type: application/json

{
  "brand": "BMW",
  "model": "3 Series",
  "year": 2023,
  "price": 55000.00,
  "numberOfDoors": 4
}
```

### Create a Motorcycle

```
POST /api/vehicles/motorcycles
Content-Type: application/json

{
  "brand": "Ducati",
  "model": "Monster",
  "year": 2023,
  "price": 18000.00,
  "hasSidecar": false
}
```

### Partial Update

```
PATCH /api/vehicles/1
Content-Type: application/json

{
  "price": 23000.00,
  "numberOfDoors": 2
}
```

Only the fields included in the body are updated.

---

## 7. Sample Requests

```bash
# List all vehicles
curl http://localhost:8080/api/vehicles

# Get a single vehicle
curl http://localhost:8080/api/vehicles/1

# Filter by brand
curl http://localhost:8080/api/vehicles/brand/Toyota

# Filter by price range
curl "http://localhost:8080/api/vehicles/price-range?min=10000&max=30000"

# Get polymorphic descriptions
curl http://localhost:8080/api/vehicles/describe

# Delete a vehicle
curl -X DELETE http://localhost:8080/api/vehicles/3
```
