# Spring Boot JPA Relationships Template

A ready-to-use Spring Boot template demonstrating **one-to-many** and **one-to-one** JPA relationships. Clone, rename the generic `Parent` / `Child` / `ChildDetail` classes to your domain, and you have a working CRUD REST API backed by JPA.

---

## Relationship Overview

```
┌─────────────────────────────────────────────────────────────────┐
│                    RELATIONSHIP DIAGRAM                         │
│                                                                 │
│   ┌──────────┐  ONE-TO-MANY   ┌──────────┐                     │
│   │  Parent  │ ─────────────► │  Child   │                     │
│   │(1 parent)│  (1 has many)  │(N childn)│                     │
│   └──────────┘                └──────────┘                     │
│                                     │  ONE-TO-ONE              │
│                                     ▼                          │
│                               ┌─────────────┐                  │
│                               │ ChildDetail │                  │
│                               └─────────────┘                  │
│                                                                 │
│  Database tables:                                               │
│    parent       (id, name, category, description, entry_year)   │
│    child        (id, name, data_value, entry_year, parent_id)   │
│    child_detail (id, child_id, description, additional_info,    │
│                  numeric_value, notes)                          │
└─────────────────────────────────────────────────────────────────┘
```

### One-to-Many (Parent → Child)
- One `Parent` row can have **many** `Child` rows.
- The FK (`parent_id`) lives in the `child` table.
- Deleting a `Parent` **cascades** and deletes all its `Children`.

### One-to-One (Child → ChildDetail)
- Each `Child` row has **at most one** `ChildDetail` row.
- The FK (`child_id`, unique) lives in the `child_detail` table.
- Deleting a `Child` **cascades** and deletes its `ChildDetail`.

---

## Project Structure

```
src/
└── main/
│   ├── java/com/example/jpademo/
│   │   ├── JpaDemoApplication.java       <- Spring Boot entry point
│   │   ├── entity/
│   │   │   ├── Parent.java               <- @OneToMany owner
│   │   │   ├── Child.java                <- @ManyToOne + @OneToOne owner
│   │   │   └── ChildDetail.java          <- @OneToOne detail (FK side)
│   │   ├── dto/
│   │   │   ├── ParentDto.java
│   │   │   ├── ChildDto.java
│   │   │   ├── ChildDetailDto.java
│   │   │   ├── CreateParentRequest.java
│   │   │   └── CreateChildRequest.java
│   │   ├── repository/
│   │   │   ├── ParentRepository.java
│   │   │   ├── ChildRepository.java
│   │   │   └── ChildDetailRepository.java
│   │   ├── service/
│   │   │   ├── ParentService.java
│   │   │   └── ChildService.java
│   │   ├── controller/
│   │   │   ├── ParentController.java     <- GET/POST/PUT/DELETE /api/parents
│   │   │   └── ChildController.java      <- GET/POST/PUT/DELETE /api/children
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.properties        <- H2 config
│       └── data.sql                      <- seed data
└── test/
    └── java/com/example/jpademo/
        └── JpaDemoApplicationTests.java  <- 7 integration tests
```

---

## Quick Start

### Prerequisites
- Java 17+
- Maven 3.8+

### Run
```bash
mvn spring-boot:run
```

### Test
```bash
mvn test
```

The app starts on **http://localhost:8080**.
H2 console: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:jpademo`
- User: `sa`
- Password: _(empty)_

---

## REST API Reference

### Parents (one-to-many side)

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/parents` | List all parents with their children |
| GET | `/api/parents/{id}` | Get one parent with its children |
| POST | `/api/parents` | Create a parent |
| PUT | `/api/parents/{id}` | Update a parent |
| DELETE | `/api/parents/{id}` | Delete a parent (cascades to children) |

### Children

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/children` | List all children |
| GET | `/api/children/{id}` | Get one child with its detail |
| GET | `/api/children?parentId={id}` | All children of a parent |
| POST | `/api/children` | Create a child (linked to a parent) |
| PUT | `/api/children/{id}/detail` | Add or replace the one-to-one detail |
| DELETE | `/api/children/{id}` | Delete a child (cascades to detail) |

### Example cURL commands

```bash
# List all parents (with their children)
curl http://localhost:8080/api/parents

# Get one parent
curl http://localhost:8080/api/parents/1

# Create a parent
curl -X POST http://localhost:8080/api/parents \
  -H "Content-Type: application/json" \
  -d '{"name":"New Parent","category":"Type X","description":"Desc","entryYear":2024}'

# Create a child linked to parent 1
curl -X POST http://localhost:8080/api/children \
  -H "Content-Type: application/json" \
  -d '{"name":"New Child","dataValue":"abc","entryYear":2024,"parentId":1}'

# Add one-to-one detail to child 1
curl -X PUT http://localhost:8080/api/children/1/detail \
  -H "Content-Type: application/json" \
  -d '{"description":"My detail","additionalInfo":"Extra","numericValue":42,"notes":"Note"}'

# Get children of parent 1
curl "http://localhost:8080/api/children?parentId=1"

# Delete a parent (and all its children)
curl -X DELETE http://localhost:8080/api/parents/1
```

---

## How to Adapt This Template for Your Exam

Follow these steps in order. Search-and-replace is your friend — most IDEs let you rename a class across all files at once (Refactor > Rename).

### Step 1 — Rename the project (`pom.xml`)
Change `artifactId`, `name`, and `description` to match your project.

### Step 2 — Rename the application name (`application.properties`)
Change `spring.application.name` to match your project.

### Step 3 — Update seed data (`data.sql`)
Replace the `INSERT` statements with data relevant to your domain.
Keep the `ALTER TABLE ... RESTART WITH 100` lines to avoid ID collisions in tests.

### Step 4 — Rename entities

#### 4a — `Parent.java` → your "one" side entity
Example: domain is **Artist -> Songs**, rename to `Artist.java`:
- Class name: `Artist`
- Table: `@Table(name = "artist")`
- Fields: replace `name`, `category`, `description`, `entryYear` with artist-specific fields
  e.g. `artistName`, `nationality`, `genre`, `debutYear`
- Collection field: rename `children` -> `songs`; rename helpers `addChild`/`removeChild` -> `addSong`/`removeSong`

#### 4b — `Child.java` → your "many" side entity
Example: `Song.java`
- Class name: `Song`
- Table: `@Table(name = "song")`
- Fields: replace `name`, `dataValue`, `entryYear` with song fields
  e.g. `title`, `durationSeconds`, `releaseYear`
- FK field: rename `parent` -> `artist`

#### 4c — `ChildDetail.java` → your detail entity
Example: `SongDetail.java`
- Class name: `SongDetail`
- Table: `@Table(name = "song_detail")`
- Fields: replace `description`, `additionalInfo`, `numericValue`, `notes` with detail fields
  e.g. `album`, `lyricsSnippet`, `durationSeconds`, `genre`
- FK field: rename `child` -> `song`

> **Reserved SQL keywords — do NOT use as column names:**
> `year`, `value`, `type`, `order`, `group`, `key`, `select`, `where`, `from`
> Use prefixed alternatives like `entry_year`, `data_value`, `record_type` instead.

### Step 5 — Rename DTOs
Rename all DTO classes and update fields to match your renamed entities:

| Old name | New name (example) |
|---|---|
| `ParentDto` | `ArtistDto` |
| `ChildDto` | `SongDto` |
| `ChildDetailDto` | `SongDetailDto` |
| `CreateParentRequest` | `CreateArtistRequest` |
| `CreateChildRequest` | `CreateSongRequest` |

### Step 6 — Rename Repositories
| Old name | New name (example) |
|---|---|
| `ParentRepository` | `ArtistRepository` |
| `ChildRepository` | `SongRepository` |
| `ChildDetailRepository` | `SongDetailRepository` |

Update any `findByCategory` / `findByParentId` method names to match your fields.

### Step 7 — Rename Services
| Old name | New name (example) |
|---|---|
| `ParentService` | `ArtistService` |
| `ChildService` | `SongService` |

Update all field references in the mapping helpers (`toDto`, `toChildDto`, etc.).

### Step 8 — Rename Controllers
| Old name | New name (example) |
|---|---|
| `ParentController` | `ArtistController` |
| `ChildController` | `SongController` |

Change `@RequestMapping`:
- `/api/parents` -> `/api/artists`
- `/api/children` -> `/api/songs`

### Step 9 — Update Tests (`JpaDemoApplicationTests.java`)
Rename the test class and update all assertions to use your new entity names and seed data values.

### Step 10 — Verify everything
```bash
mvn clean test
```
All tests should pass. If they fail, the error message points to the mismatched field.

---

## Key JPA Annotations Explained

| Annotation | Where used | What it does |
|---|---|---|
| `@OneToMany(mappedBy="parent", cascade=ALL, orphanRemoval=true)` | Parent | Declares one-to-many. `mappedBy` points to the FK field in Child. `cascade=ALL` propagates save/delete. `orphanRemoval=true` deletes children removed from the list. |
| `@ManyToOne` + `@JoinColumn(name="parent_id")` | Child | Many-to-one back-reference. Places the FK column in the child table. This is the **owning** side of the relationship. |
| `@OneToOne(mappedBy="child", cascade=ALL, orphanRemoval=true)` | Child | Declares one-to-one (inverse side). No FK column here. |
| `@OneToOne` + `@JoinColumn(name="child_id", unique=true)` | ChildDetail | One-to-one (owning / FK side). Places the FK column in this table. `unique=true` enforces the constraint at DB level. |
| `@EntityGraph(attributePaths="children")` | Repository | Fetches the named collection in the same SQL query, preventing N+1 problems with lazy loading. |
| `fetch = FetchType.LAZY` | All relations | Relations are only loaded when accessed. Avoids unnecessary DB queries. |

---

## Common Pitfalls

**1. Bidirectional sync**
Always use `parent.addChild(child)` (the helper method), not just `child.setParent(parent)`.
The helper method updates both sides of the relationship in memory so queries within the same transaction return correct data.

**2. Cascade delete with lazy collections**
The `delete()` service method loads children *eagerly* before deleting the parent.
Without this, Hibernate skips the cascade and the DB throws a FK constraint violation.

**3. Reserved SQL keywords as column names**
Avoid `year`, `value`, `type`, `order`, `key` as field/column names.
Hibernate generates DDL using them unquoted, which H2 (and other DBs) reject as syntax errors.

**4. ID sequence collision in tests**
When `data.sql` inserts rows with explicit IDs (1, 2, 3...), the auto-increment sequence does not advance automatically.
Fix: add `ALTER TABLE ... ALTER COLUMN id RESTART WITH 100` at the end of `data.sql`.
