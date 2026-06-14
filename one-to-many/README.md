# Spring Boot JPA One-to-Many Template

A minimal, ready-to-use Spring Boot template demonstrating the **one-to-many** JPA relationship. Only two entities — `Parent` and `Child`. Rename them to your domain and you have a working CRUD REST API.

> Need one-to-one too? See the root project which adds `ChildDetail` for that pattern.

---

## Relationship Overview

```
┌─────────────────────────────────────────────────────┐
│               ONE-TO-MANY DIAGRAM                   │
│                                                     │
│   ┌──────────┐           ┌──────────┐               │
│   │  Parent  │ 1 ──────► │  Child   │               │
│   │          │   (many)  │          │               │
│   └──────────┘           └──────────┘               │
│                                                     │
│  parent table: id, name, category, description,     │
│                entry_year                           │
│                                                     │
│  child table:  id, name, data_value, entry_year,    │
│                parent_id  <-- FK lives here         │
└─────────────────────────────────────────────────────┘
```

- One `Parent` can have **many** `Child` rows.
- The FK (`parent_id`) lives in the `child` table — this is the **owning** side.
- Deleting a `Parent` **cascades** and automatically deletes all its `Children`.
- A `Child` can be **re-parented** (moved to a different Parent) via the update endpoint.

---

## Project Structure

```
src/
└── main/
│   ├── java/com/example/onetomany/
│   │   ├── OneManyApplication.java       <- Spring Boot entry point
│   │   ├── entity/
│   │   │   ├── Parent.java               <- @OneToMany side (annotated)
│   │   │   └── Child.java                <- @ManyToOne side (owns the FK)
│   │   ├── dto/
│   │   │   ├── ParentDto.java
│   │   │   ├── ChildDto.java
│   │   │   ├── CreateParentRequest.java
│   │   │   └── CreateChildRequest.java
│   │   ├── repository/
│   │   │   ├── ParentRepository.java
│   │   │   └── ChildRepository.java
│   │   ├── service/
│   │   │   ├── ParentService.java
│   │   │   └── ChildService.java
│   │   ├── controller/
│   │   │   ├── ParentController.java     <- /api/parents
│   │   │   └── ChildController.java      <- /api/children
│   │   └── exception/
│   │       ├── ResourceNotFoundException.java
│   │       └── GlobalExceptionHandler.java
│   └── resources/
│       ├── application.properties
│       └── data.sql                      <- seed data (3 parents, 7 children)
└── test/
    └── java/com/example/onetomany/
        └── OneManyApplicationTests.java  <- 12 integration tests
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

App runs on **http://localhost:8080**.
H2 console: **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:mem:onetomanydb`
- User: `sa` / Password: _(empty)_

---

## REST API Reference

### Parents

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/parents` | All parents, each with its full children list |
| GET | `/api/parents/{id}` | One parent with its children |
| POST | `/api/parents` | Create a parent |
| PUT | `/api/parents/{id}` | Update a parent |
| DELETE | `/api/parents/{id}` | Delete parent and all its children (cascade) |

### Children

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/children` | All children |
| GET | `/api/children/{id}` | One child |
| GET | `/api/children?parentId={id}` | All children of a specific parent |
| POST | `/api/children` | Create a child linked to a parent |
| PUT | `/api/children/{id}` | Update a child (can re-parent to a different parent) |
| DELETE | `/api/children/{id}` | Delete a child |

### Example cURL commands

```bash
# List all parents with children
curl http://localhost:8080/api/parents

# Get one parent with its children
curl http://localhost:8080/api/parents/1

# Create a parent
curl -X POST http://localhost:8080/api/parents \
  -H "Content-Type: application/json" \
  -d '{"name":"New Parent","category":"Type X","description":"Desc","entryYear":2024}'

# Create a child for parent 1
curl -X POST http://localhost:8080/api/children \
  -H "Content-Type: application/json" \
  -d '{"name":"New Child","dataValue":"abc","entryYear":2024,"parentId":1}'

# Get all children of parent 1
curl "http://localhost:8080/api/children?parentId=1"

# Move child 4 from its current parent to parent 1 (re-parent)
curl -X PUT http://localhost:8080/api/children/4 \
  -H "Content-Type: application/json" \
  -d '{"name":"Child 2-A","dataValue":"value-2a","entryYear":2016,"parentId":1}'

# Delete a child
curl -X DELETE http://localhost:8080/api/children/1

# Delete a parent (and ALL its children)
curl -X DELETE http://localhost:8080/api/parents/1
```

---

## How to Adapt for Your Exam

### Step 1 — `pom.xml`
Change `artifactId`, `name`, `description` to your project name.

### Step 2 — `application.properties`
Change `spring.application.name`.

### Step 3 — `data.sql`
Replace the INSERT statements with your domain data.
Keep the `ALTER TABLE ... RESTART WITH 100` lines.

### Step 4 — Rename `Parent.java`

| Replace | With your domain |
|---|---|
| Class `Parent` | e.g. `Department`, `Category`, `Author` |
| `@Table(name = "parent")` | `@Table(name = "department")` |
| Field `name` | e.g. `deptName` |
| Field `category` | e.g. `location` |
| Field `description` | e.g. `budget` |
| Field `entryYear` | e.g. `foundedYear` |
| `List<Child> children` | `List<Employee> employees` |
| `addChild` / `removeChild` | `addEmployee` / `removeEmployee` |

### Step 5 — Rename `Child.java`

| Replace | With your domain |
|---|---|
| Class `Child` | e.g. `Employee`, `Product`, `Song` |
| `@Table(name = "child")` | `@Table(name = "employee")` |
| Field `name` | e.g. `fullName` |
| Field `dataValue` | e.g. `salary` (avoid `value` — reserved) |
| Field `entryYear` | e.g. `hireYear` (avoid `year` — reserved) |
| `@JoinColumn(name = "parent_id")` | `@JoinColumn(name = "dept_id")` |
| Field `parent` (ManyToOne) | `department` |

### Step 6 — Update DTOs
Rename `ParentDto`, `ChildDto`, `CreateParentRequest`, `CreateChildRequest` and update all fields.

### Step 7 — Update Repositories
Rename and update finders. The `findByParentId` in `ChildRepository` becomes e.g. `findByDepartmentId`.

### Step 8 — Update Services
Rename services and update all field references in `toDto` / `toChildDto` mapping helpers.

### Step 9 — Update Controllers
- Rename controllers
- Change `@RequestMapping("/api/parents")` to e.g. `@RequestMapping("/api/departments")`
- Change `@RequestMapping("/api/children")` to e.g. `@RequestMapping("/api/employees")`

### Step 10 — Run tests
```bash
mvn clean test
```

---

## Key JPA Annotations

```java
// ---- Parent.java (ONE side) ----

@OneToMany(
    mappedBy    = "parent",          // field in Child that holds the FK
    cascade     = CascadeType.ALL,   // save/delete propagates to children
    orphanRemoval = true,            // delete DB row when child removed from list
    fetch       = FetchType.LAZY     // children only loaded when accessed
)
private List<Child> children = new ArrayList<>();


// ---- Child.java (MANY side — OWNS the FK column) ----

@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "parent_id", nullable = false)  // actual FK column in child table
private Parent parent;
```

### Why `addChild()` matters

```java
// Wrong — only sets one side; in-memory list is stale within same transaction
child.setParent(parent);

// Correct — updates both sides; in-memory queries return consistent results
parent.addChild(child);   // calls child.setParent(this) internally
```

### Why `findWithChildrenById` uses `@EntityGraph`

Without it, accessing `parent.getChildren()` triggers a separate SQL query for every parent (**N+1 problem**). `@EntityGraph` tells Hibernate to fetch children in the same query using a JOIN.

---

## Common Pitfalls

| Pitfall | Fix |
|---|---|
| Reserved keywords as column names (`year`, `value`, `type`, `key`) | Use `entryYear`, `dataValue`, `category`, etc. |
| FK constraint error on parent delete | Load children list before deleting (already done in `delete()` service method) |
| `children` list empty after creating child in same transaction | Use `parent.addChild(child)` not just `child.setParent(parent)` |
| ID collision between seed data and new inserts in tests | Add `ALTER TABLE ... RESTART WITH 100` at end of `data.sql` |
