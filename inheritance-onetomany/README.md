# Spring Boot JPA Inheritance + One-to-Many Template

Combines two JPA patterns in one project:

1. **One-to-Many** — `Owner` (1) → `Item` (N)
2. **Inheritance (SINGLE_TABLE)** — `Item` is abstract; `ItemTypeA` and `ItemTypeB` are concrete subtypes

One owner can have many items, and those items can be of different subtypes — all stored in a single `item` table.

---

## Relationship + Inheritance Diagram

```
┌──────────────────────────────────────────────────────────────────┐
│                    COMBINED PATTERN                              │
│                                                                  │
│   ┌───────────┐  ONE-TO-MANY   ┌─────────────────────────┐      │
│   │   Owner   │ 1 ──────────►  │  Item  (abstract base)  │      │
│   └───────────┘   (many items) └─────────┬───────────────┘      │
│                                          │  SINGLE_TABLE        │
│                                          │  INHERITANCE         │
│                              ┌───────────┴────────────┐         │
│                              ▼                        ▼         │
│                        ┌──────────┐            ┌──────────┐     │
│                        │ItemTypeA │            │ItemTypeB │     │
│                        └──────────┘            └──────────┘     │
│                                                                  │
│  Database tables:                                                │
│                                                                  │
│    owner  (id, name, category, entry_year)                       │
│                                                                  │
│    item   (id, item_type, name, description, entry_year,         │
│            extra_field_a,               ← TypeA column          │
│            extra_field_b, numeric_field,← TypeB columns         │
│            owner_id)                    ← FK to owner           │
└──────────────────────────────────────────────────────────────────┘
```

- `item_type` is the **discriminator column** — Hibernate writes `"TYPE_A"` or `"TYPE_B"` automatically.
- `owner_id` is the **FK column** for the one-to-many relationship.
- TypeA-specific columns are `NULL` for TypeB rows (and vice versa) — this is normal for SINGLE_TABLE.

---

## Project Structure

```
src/
└── main/
│   ├── java/com/example/inheritanceonetomany/
│   │   ├── InheritanceOneManyApplication.java
│   │   ├── entity/
│   │   │   ├── Owner.java        <- @OneToMany side
│   │   │   ├── Item.java         <- abstract @Entity, @Inheritance + @ManyToOne Owner
│   │   │   ├── ItemTypeA.java    <- @DiscriminatorValue("TYPE_A")
│   │   │   └── ItemTypeB.java    <- @DiscriminatorValue("TYPE_B")
│   │   ├── dto/
│   │   │   ├── OwnerDto.java     <- includes List<ItemDto> (mixed types)
│   │   │   ├── ItemDto.java      <- flat DTO with itemType + all subtype fields
│   │   │   ├── CreateOwnerRequest.java
│   │   │   ├── CreateItemTypeARequest.java
│   │   │   └── CreateItemTypeBRequest.java
│   │   ├── repository/
│   │   │   ├── OwnerRepository.java
│   │   │   └── ItemRepository.java
│   │   ├── service/
│   │   │   ├── OwnerService.java
│   │   │   └── ItemService.java
│   │   ├── controller/
│   │   │   ├── OwnerController.java  <- /api/owners
│   │   │   └── ItemController.java   <- /api/items
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.properties
│       └── data.sql
└── test/
    └── java/com/example/inheritanceonetomany/
        └── InheritanceOneManyTests.java  <- 13 integration tests
```

---

## Quick Start

```bash
mvn spring-boot:run   # starts on http://localhost:8080
mvn test              # runs all 13 tests
```

H2 console: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:inhonetomanydb`
- User: `sa` / Password: _(empty)_

---

## REST API Reference

### Owners

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/owners` | All owners, each with their full item list (mixed subtypes) |
| GET | `/api/owners/{id}` | One owner with items |
| POST | `/api/owners` | Create an owner |
| PUT | `/api/owners/{id}` | Update an owner |
| DELETE | `/api/owners/{id}` | Delete owner + cascade all items |

### Items

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/items` | All items (any subtype) across all owners |
| GET | `/api/items/{id}` | One item |
| GET | `/api/items?ownerId=1` | All items of an owner (any subtype) |
| GET | `/api/items?ownerId=1&subtype=TYPE_A` | Only TypeA items of an owner |
| GET | `/api/items?ownerId=1&subtype=TYPE_B` | Only TypeB items of an owner |
| POST | `/api/items/type-a` | Create a TypeA item linked to an owner |
| POST | `/api/items/type-b` | Create a TypeB item linked to an owner |
| DELETE | `/api/items/{id}` | Delete any item |

### Example cURL commands

```bash
# Get owner 1 with all its items (mixed TypeA + TypeB)
curl http://localhost:8080/api/owners/1

# Get only TypeA items of owner 1
curl "http://localhost:8080/api/items?ownerId=1&subtype=TYPE_A"

# Create a TypeA item for owner 2
curl -X POST http://localhost:8080/api/items/type-a \
  -H "Content-Type: application/json" \
  -d '{"name":"My Item","description":"desc","entryYear":2024,"ownerId":2,"extraFieldA":"some-value"}'

# Create a TypeB item for owner 1
curl -X POST http://localhost:8080/api/items/type-b \
  -H "Content-Type: application/json" \
  -d '{"name":"My Item B","description":"desc","entryYear":2024,"ownerId":1,"extraFieldB":"info","numericField":42}'

# Delete an owner (cascades to ALL items, any subtype)
curl -X DELETE http://localhost:8080/api/owners/1
```

---

## How to Adapt for Your Exam

### Step 1 — Identify your domain
Define: what is the "one" side? What are the subtypes on the "many" side?

Example: **Library** (owner) → **Book** items, where books can be `PhysicalBook` or `EBook`.

### Step 2 — Rename `Owner.java`
- Class: `Library`
- Table: `@Table(name = "library")`
- Update fields, collection name (`items` → `books`), and helpers (`addItem` → `addBook`)

### Step 3 — Rename `Item.java` (the abstract base)
- Class: `Book` (abstract)
- Table: `@Table(name = "book")`
- Update fields (`name` → `title`, `entryYear` → `publishYear`, etc.)
- Update `@ManyToOne` back-reference (`owner` → `library`, `@JoinColumn(name="library_id")`)

### Step 4 — Rename `ItemTypeA.java` and `ItemTypeB.java`
- `PhysicalBook extends Book` with `@DiscriminatorValue("PHYSICAL")` and fields like `pageCount`
- `EBook extends Book` with `@DiscriminatorValue("EBOOK")` and fields like `fileSizeMb`, `downloadUrl`

### Step 5 — Update `data.sql`
- Rename tables and columns to match
- Keep `@Column(name = "extra_field_a")` pattern for any field ending in a single capital letter

### Step 6 — Update DTOs, Repositories, Services, Controllers
Follow the same rename pattern. The service's `toItemDto()` `instanceof` block is the key mapping logic — update it for your subtypes.

### Step 7 — Verify
```bash
mvn clean test
```

---

## Key Annotations Together

```java
// ---- Owner.java — the ONE side ----
@OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
private List<Item> items = new ArrayList<>();

// ---- Item.java — abstract base, MANY side + INHERITANCE base ----
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "item_type")
public abstract class Item {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)  // FK to owner
    private Owner owner;
}

// ---- ItemTypeA.java — concrete subtype ----
@Entity
@DiscriminatorValue("TYPE_A")
public class ItemTypeA extends Item {
    @Column(name = "extra_field_a")  // explicit name — Hibernate naming edge case
    private String extraFieldA;
}
```

## Important Notes

1. **Do NOT map the discriminator column as a `@Column` field** in the abstract base class. Hibernate manages `item_type` entirely; declaring it as a field breaks DDL generation (subtype columns are excluded from the CREATE TABLE).

2. **Use explicit `@Column(name=...)` for fields ending in a single capital letter**. Hibernate converts `extraFieldA` → `extra_fielda` (not `extra_field_a`), which won't match hand-written SQL.

3. **`owner.addItem(item)` is required** (not just `item.setOwner(owner)`). The helper method keeps both sides of the bidirectional relationship consistent within the same transaction.
