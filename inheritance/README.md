# Spring Boot JPA Inheritance Template (SINGLE_TABLE)

A minimal Spring Boot template demonstrating **JPA Inheritance** using the `SINGLE_TABLE` strategy. Two concrete subtypes (`ItemTypeA`, `ItemTypeB`) extend an abstract base (`Item`). All rows share one database table; a discriminator column identifies the Java subtype.

> Need inheritance **combined with one-to-many**? See the `inheritance-onetomany/` project on branch `claude/spring-boot-inheritance-onetomany`.

---

## Inheritance Strategy Diagram

```
┌─────────────────────────────────────────────────────────────────┐
│              SINGLE_TABLE INHERITANCE                           │
│                                                                 │
│   ┌──────────────────────────┐                                  │
│   │  Item  (abstract base)   │  @Inheritance(SINGLE_TABLE)      │
│   │  - id                    │  @DiscriminatorColumn("item_type")│
│   │  - name                  │                                  │
│   │  - description           │                                  │
│   │  - entryYear             │                                  │
│   └────────────┬─────────────┘                                  │
│                │                                                │
│       ┌────────┴────────┐                                       │
│       ▼                 ▼                                       │
│  ┌──────────┐      ┌──────────┐                                 │
│  │ItemTypeA │      │ItemTypeB │                                 │
│  │ TYPE_A   │      │ TYPE_B   │  <- discriminator values        │
│  │extraFieldA│     │extraFieldB│                                │
│  └──────────┘      │numericField│                               │
│                    └──────────┘                                 │
│                                                                 │
│  Single database table: item                                    │
│  ┌────┬──────────┬──────┬─────────┬────────────┬───────────┐   │
│  │ id │item_type │ name │  desc   │extra_field_a│extra_fld_b│   │
│  ├────┼──────────┼──────┼─────────┼────────────┼───────────┤   │
│  │  1 │ TYPE_A   │ ...  │  ...    │  alpha-1   │   NULL    │   │
│  │  2 │ TYPE_B   │ ...  │  ...    │   NULL     │   beta-1  │   │
│  └────┴──────────┴──────┴─────────┴────────────┴───────────┘   │
└─────────────────────────────────────────────────────────────────┘
```

---

## Three Inheritance Strategies

| Strategy | Table layout | Pros | Cons |
|---|---|---|---|
| **SINGLE_TABLE** ← this project | 1 table, discriminator column | Fast (no joins) | Nullable columns for subtype fields |
| **JOINED** | 1 base table + 1 per subtype | Normalised, no nulls | JOIN on every query |
| **TABLE_PER_CLASS** | 1 full table per concrete class | No joins, no nulls | No shared sequence; UNION ALL for polymorphic queries |

To switch strategy, change the one annotation in `Item.java`:
```java
// SINGLE_TABLE (current)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")

// JOINED (normalised)
@Inheritance(strategy = InheritanceType.JOINED)
// remove @DiscriminatorColumn — not used by JOINED

// TABLE_PER_CLASS
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
// remove @DiscriminatorColumn — not used
```

---

## Project Structure

```
src/
└── main/
│   ├── java/com/example/inheritance/
│   │   ├── InheritanceApplication.java
│   │   ├── entity/
│   │   │   ├── Item.java           <- abstract @Entity, SINGLE_TABLE
│   │   │   ├── ItemTypeA.java      <- @DiscriminatorValue("TYPE_A")
│   │   │   └── ItemTypeB.java      <- @DiscriminatorValue("TYPE_B")
│   │   ├── dto/
│   │   │   ├── ItemDto.java        <- flat DTO with all subtype fields
│   │   │   ├── CreateItemTypeARequest.java
│   │   │   └── CreateItemTypeBRequest.java
│   │   ├── repository/
│   │   │   └── ItemRepository.java <- JpaRepository<Item, Long>
│   │   ├── service/
│   │   │   └── ItemService.java
│   │   ├── controller/
│   │   │   └── ItemController.java
│   │   └── exception/
│   └── resources/
│       ├── application.properties
│       └── data.sql                <- 3 TypeA + 3 TypeB seed rows
└── test/
    └── java/com/example/inheritance/
        └── InheritanceApplicationTests.java  <- 11 tests
```

---

## Quick Start

```bash
mvn spring-boot:run   # http://localhost:8080
mvn test
```

H2 console: **http://localhost:8080/h2-console** (URL: `jdbc:h2:mem:inheritancedb`, user: `sa`)

---

## REST API Reference

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/items` | All items (mixed TypeA + TypeB) |
| GET | `/api/items/{id}` | One item — response shows correct subtype |
| GET | `/api/items?subtype=TYPE_A` | Filter to TypeA only |
| GET | `/api/items?subtype=TYPE_B` | Filter to TypeB only |
| POST | `/api/items/type-a` | Create a TypeA item |
| POST | `/api/items/type-b` | Create a TypeB item |
| DELETE | `/api/items/{id}` | Delete any item |

```bash
# All items (mixed)
curl http://localhost:8080/api/items

# TypeA items only
curl "http://localhost:8080/api/items?subtype=TYPE_A"

# Create TypeA
curl -X POST http://localhost:8080/api/items/type-a \
  -H "Content-Type: application/json" \
  -d '{"name":"My A","description":"desc","entryYear":2024,"extraFieldA":"val-a"}'

# Create TypeB
curl -X POST http://localhost:8080/api/items/type-b \
  -H "Content-Type: application/json" \
  -d '{"name":"My B","description":"desc","entryYear":2024,"extraFieldB":"val-b","numericField":42}'
```

---

## How to Adapt for Your Exam

### Step 1 — Rename `Item.java` (the base)
Replace with your abstract base type (e.g. `Vehicle`, `Payment`, `Employee`):
- Class name, table name
- Fields: `name`, `description`, `entryYear` → your domain fields
- **Do NOT add a `@Column` mapping for the discriminator column** — Hibernate manages it

### Step 2 — Rename `ItemTypeA.java`
Replace with your first concrete subtype (e.g. `Car`, `CreditCard`, `Manager`):
- Class name, `@DiscriminatorValue("TYPE_A")` → `@DiscriminatorValue("CAR")`
- Fields: `extraFieldA` → your subtype-specific fields
- Keep `@Column(name = "extra_field_a")` pattern for fields ending in a single capital letter

### Step 3 — Rename `ItemTypeB.java`
Same pattern for your second concrete subtype (e.g. `Truck`, `BankTransfer`, `Employee`).

### Step 4 — Update `ItemDto`
Add/remove fields to match. The `itemType` field is the discriminator returned to clients.

### Step 5 — Update `ItemService.toDto()`
The `instanceof` block is the key — update it for your renamed subtypes:
```java
if (item instanceof Car car) {
    builder.itemType("CAR").numDoors(car.getNumDoors());
} else if (item instanceof Truck truck) {
    builder.itemType("TRUCK").payloadTons(truck.getPayloadTons());
}
```

### Step 6 — Update `ItemRepository`, controller, and tests
### Step 7 — Run `mvn clean test`

---

## Key Annotations

```java
// Abstract base — declares the strategy and discriminator column
@Entity
@Table(name = "item")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
public abstract class Item { ... }

// Concrete subtype — declares its discriminator value
@Entity
@DiscriminatorValue("TYPE_A")
public class ItemTypeA extends Item {
    @Column(name = "extra_field_a")   // explicit name avoids Hibernate naming edge case
    private String extraFieldA;
}

// Repository — works polymorphically with all subtypes
public interface ItemRepository extends JpaRepository<Item, Long> {
    @Query("SELECT a FROM ItemTypeA a")
    List<ItemTypeA> findAllTypeA();   // Hibernate adds WHERE item_type = 'TYPE_A'
}
```

## Important Notes

1. **Do NOT map the discriminator column as a `@Column` field** in the abstract class. This prevents Hibernate from adding subtype columns to the DDL.

2. **Use `@Column(name=...)` for fields ending in a single capital letter** (e.g. `extraFieldA`). Hibernate converts it to `extra_fielda`, not `extra_field_a`.

3. **Use `instanceof` pattern matching** in the service to access subtype-specific fields safely.
