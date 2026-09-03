# Spring-Book — Library Management System

A mentored, from-scratch Spring Boot backend project built to learn backend development with Java/Spring Boot, following a "language-first → build-first → framework" learning philosophy. This README documents the full journey from plain Java through OOP concepts to Spring Boot, including architecture decisions, testing strategy, external integrations, and deployment with Docker.

---

## Table of Contents

- [Motivation & Learning Approach](#motivation--learning-approach)
- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Project Timeline](#project-timeline)
  - [Phase 0: Foundations](#phase-0-foundations)
  - [Phase 1: Database Layer](#phase-1-database-layer)
  - [Phase 2: REST API Layer](#phase-2-rest-api-layer)
  - [Phase 3: Architecture Refactor](#phase-3-architecture-refactor)
  - [Phase 4: Testing Suite](#phase-4-testing-suite)
  - [Phase 5: API Documentation](#phase-5-api-documentation)
  - [Phase 6: Containerization with Docker](#phase-6-containerization-with-docker)
  - [Phase 7: Open Library Integration](#phase-7-open-library-integration)
  - [Phase 8: GraphQL Query Layer](#phase-8-graphql-query-layer)
- [Key Design Decisions](#key-design-decisions)
- [Lessons Learned](#lessons-learned)
- [Roadmap / What's Next](#roadmap--whats-next)

---
## Motivation & Learning Approach

After attempting several Spring Boot courses and repeatedly feeling lost from jumping in at mid-concept entry points, I settled on an approach that mirrored how I successfully learned Flutter: **learn the language → learn the patterns → learn the framework**.
 
This project was built through a strict **mentor-only dynamic**: tasks were assigned, code was reviewed, and hints were given — but code was only written *for* me on explicit request. I wrote 99.6% of the codebase myself, debugging and researching independently with mentorship on design decisions and architectural patterns rather than syntax.
 
Before touching Spring, I built a plain **Java/Kotlin Library Management System** through six OOP phases (encapsulation, inheritance, abstraction, interfaces, polymorphism, composition/statics) to force fluency with object-oriented design before introducing framework magic. This foundational work directly informed architectural choices in the Spring version (e.g., inheritance strategy for entities, service layer organization, exception hierarchies).

---

## Tech Stack

| Layer            | Technology                                                                  |
|------------------|-----------------------------------------------------------------------------|
| Language         | Java 21 / Kotlin 1.9                                                        |
| Framework        | Spring Boot 4.x                                                             |
| Persistence      | Spring Data JPA / Hibernate                                                 |
| Database         | PostgreSQL                                                                  |
| Build Tool       | Maven                                                                       |
| Testing          | JUnit 5, Mockito, AssertJ, `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest` |
| API Docs         | Swagger/OpenAPI 3.0                                                         |
| Query API        | GraphQL (Spring GraphQL / graphql-java)                                      |
| External APIs    | Open Library API (REST client)                                              |
| Containerization | Docker & Docker Compose                                                     |
| Security         | Spring Security / JWT                                                       |

---

## Architecture Overview

The project follows **Clean Architecture** principles — the same mental model I use in Flutter — adapted to Spring Boot's conventions:

```
Controller Layer   → REST endpoints, request/response DTOs, validation
GraphQL Layer      → Query resolvers, schema-driven type mapping
Service Layer      → Business logic, split by entity + an orchestrator for cross-entity ops
Repository Layer   → Spring Data JPA interfaces, custom derived queries
Model Layer        → JPA entities, JOINED inheritance for the Member hierarchy
Integration Layer  → External API clients (Open Library), data transformation
```

**Core entities:** `Book`, `Member` (base class), `Student` / `Faculty` (subclasses via JOINED inheritance), `Loan`.

**Service layer structure:**
- `BookService`, `MemberService`, `LoanService` — own their respective entity's logic
- `LibraryService` — orchestrator for cross-entity operations (`borrowBook`, `returnBook`, `getTotalTransactions`), following a rule I proposed myself: `LoanService` owns active-loan existence checks, while `LibraryService` orchestrates the high-level borrow/return workflows.

**External integrations:**
- `OpenLibraryClient` — REST client wrapping the Open Library API for book discovery by ISBN or query
- `OpenLibraryMapper` — transforms Open Library responses into `CreateBookRequest` DTOs
- `OpenLibraryImportRunner` — Spring profile-driven batch importer (activated with `spring.profiles.active=import`)

**Soft-delete pattern:** Loan history is preserved via a nullable `returnDate` column rather than physically deleting loan records — an active loan is one where `returnDate IS NULL`.
 

## Project Structure

```
 library/
    ├── src/
    │   ├── main/
    │   │   ├── java/com/phly101/library/
    │   │   │   ├── controller/
    │   │   │   │   ├── BookController.java
    │   │   │   │   ├── LoanController.java
    │   │   │   │   └── MemberController.java
    │   │   │   ├── dto/
    │   │   │   │   ├── book/
    │   │   │   │   │   ├── BookResponse.java
    │   │   │   │   │   ├── CreateBookRequest.java
    │   │   │   │   │   └── UpdateBookRequest.java
    │   │   │   │   ├── common/
    │   │   │   │   │   ├── ErrorResponseRecord.java
    │   │   │   │   │   └── ValidationErrorResponseRecord.java
    │   │   │   │   ├── loan/
    │   │   │   │   │   ├── CreateLoanRequest.java
    │   │   │   │   │   ├── LoanResponse.java
    │   │   │   │   │   └── TransactionCountResponse.java
    │   │   │   │   └── member/
    │   │   │   │       ├── CreateMemberRequest.java
    │   │   │   │       ├── MemberResponse.java
    │   │   │   │       └── UpdateMemberRequest.java
    │   │   │   ├── exception/
    │   │   │   │   ├── handler/
    │   │   │   │   │   └── GlobalExceptionHandler.java
    │   │   │   ├── mapper/
    │   │   │   │   ├── BookMapper.java
    │   │   │   │   ├── LoanMapper.java
    │   │   │   │   └── MemberMapper.java
    │   │   │   ├── model/
    │   │   │   │   ├── enums/
    │   │   │   │   │   ├── FacultyRole.java
    │   │   │   │   │   └── MemberType.java
    │   │   │   │   ├── Book.java
    │   │   │   │   ├── Borrowable.java
    │   │   │   │   ├── Faculty.java
    │   │   │   │   ├── Loan.java
    │   │   │   │   ├── Member.java
    │   │   │   │   └── Student.java
    │   │   │   ├── repository/
    │   │   │   │   ├── BookRepository.java
    │   │   │   │   ├── LoanRepository.java
    │   │   │   │   └── MemberRepository.java
    │   │   │   ├── service/
    │   │   │   │   ├── BookService.java
    │   │   │   │   ├── LoanService.java
    │   │   │   │   ├── LibraryService.java
    │   │   │   │   └── MemberService.java
    │   │   │   └── LibraryApplication.java
    │   │   ├── kotlin/com/phly101/library/
    │   │   │   ├── dto/
    │   │   │   │   ├── MemberProfileDto.kt
    │   │   │   │   └── LoanHistoryItemDto.kt
    │   │   │   ├── graphql/
    │   │   │   │   └── MemberGraphQLResolver.kt
    │   │   │   ├── integration/
    │   │   │   │   └── openlibrary/
    │   │   │   │       ├── OpenLibraryClient.kt
    │   │   │   │       ├── OpenLibraryMapper.kt
    │   │   │   │       ├── OpenLibraryImportRunner.kt
    │   │   │   │       ├── PublishDateFormatter.kt
    │   │   │   │       └── dto/
    │   │   │   │           ├── SearchResponse.kt
    │   │   │   │           ├── SearchDoc.kt
    │   │   │   │           ├── OpenLibraryBookData.kt
    │   │   │   │           ├── OpenLibraryAuthor.kt
    │   │   │   │           └── OpenLibraryCover.kt
    │   │   │   └── mapper/
    │   │   │       └── Mapper.kt
    │   │   └── resources/
    │   │       ├── application.yml
    │   │       ├── banner.txt
    │   │       └── graphql/
    │   │           └── schema.graphqls
    │   └── test/
    │       └── java/com/phly101/library/
    │           ├── controller/
    │           │   ├── BookControllerTest.java
    │           │   ├── LoanControllerTest.java
    │           │   └── MemberControllerTest.java
    │           ├── librarySpringTest/
    │           │   └── LibraryEndToEndTest.java
    │           ├── repository/
    │           │   ├── BookRepositoryTest.java
    │           │   ├── LoanRepositoryTest.java
    │           │   └── MemberRepositoryTest.java
    │           └── service/
    │               ├── BookServiceTest.java
    │               ├── LoanServiceTest.java
    │               ├── LibraryServiceTest.java
    │               └── MemberServiceTest.java
    ├── Dockerfile
    ├── docker-compose.yml
    ├── .env.example
    ├── pom.xml
    └── README.md
```

**Key directories:**
- `controller/` — REST endpoints for Books, Members, and Loans
- `graphql/` — GraphQL resolvers and query handlers (Kotlin)
- `integration/openlibrary/` — Open Library API client, mappers, and batch import runner (Kotlin)
- `dto/` — Request/response records and Kotlin data classes organized by entity type
- `exception/` — Custom exception hierarchy and global exception handler
- `mapper/` — Entity ↔ DTO converters (Java and Kotlin)
- `model/` — JPA entities and enums
- `repository/` — Spring Data JPA interfaces with custom queries
- `service/` — Business logic organized by entity + orchestrator
- `resources/graphql/` — GraphQL schema definition files
- `test/` — Mirror structure of main codebase: controller slices, service unit tests, repository integration tests, and end-to-end tests

---

## API Documentation

The API is fully documented using **Swagger/OpenAPI 3.0**, accessible at `http://localhost:8080/swagger-ui.html` when the application is running.

### Endpoint Coverage

All six REST endpoints are documented with request/response schemas, parameter details, and real-world examples:

**Books:**
- `POST /books` — Add a new book to the catalog
- `GET /books/{isbn}` — Fetch a specific book by ISBN

**Members:**
- `POST /members` — Register a new library member (Student or Faculty)
- `PUT /members/{id}` — Update member information

**Loans:**
- `POST /loans` — Borrow a book (creates an active loan)
- `DELETE /loans` — Return a book (closes the loan via soft-delete)

### GraphQL Endpoint

**Query endpoint:** `POST /graphql`

**Available queries:**
- `memberProfile(memberId: ID!)` — Fetch a member's profile including their full loan history with book details (ISBN, title, borrow/return dates)

**Example query:**
```graphql
query {
  memberProfile(memberId: "member-uuid-here") {
    id
    name
    memberType
    loanHistory {
      loanId
      bookTitle
      bookIsbn
      returnDate
    }
  }
}
```

Interactive GraphQL explorer available at `http://localhost:8080/graphiql` when the application is running.

### Request/Response Schemas

The following schemas are automatically generated and documented in Swagger:

**Request DTOs:**
- `CreateBookRequest` — ISBN, title, author
- `UpdateBookRequest` — Allows title/author updates
- `CreateMemberRequest` — Name, type (STUDENT/FACULTY), optional Faculty role
- `UpdateMemberRequest` — Name and role updates
- `CreateLoanRequest` — ISBN and member ID

**Response DTOs:**
- `BookResponse` — Book details with ISBN, title, author
- `MemberResponse` — Member info with name, type, and unique member ID
- `LoanResponse` — Loan history including borrow/return dates
- `TransactionCountResponse` — Total number of completed transactions

### Example Endpoints

**Fetch a book by ISBN:**

<img width="1281" height="897" alt="Sleekshot 2026-08-18 11-32-54" src="https://github.com/user-attachments/assets/0878fa3a-8ab7-41ce-b30d-1b7dbd1916e4" />


Request parameters and response codes with example JSON bodies.

**Available Schemas:**

<img width="1427" height="616" alt="WhatsApp Image 2026-08-18 at 11 24 04" src="https://github.com/user-attachments/assets/efbf9471-a6c5-4825-bc3b-8168a570a47d" />


Complete list of request/response objects with field descriptions and enum values (e.g., `MemberType: STUDENT | FACULTY`).

**Detailed Schema Example:**

<img width="1437" height="512" alt="WhatsApp Image 2026-08-18 at 11 23 52" src="https://github.com/user-attachments/assets/5d39fa12-4d58-4a64-aef8-3e7392ceb926" />


Full schema breakdown showing `type` (with enum options), `name`, `memberId`, and example values.

**API Endpoints Overview**

<img width="1282" height="659" alt="Sleekshot 2026-08-18 11-33-17" src="https://github.com/user-attachments/assets/2db5300c-e24b-4b86-880f-5f43aad63903" />


A comprehensive view of the REST API endpoints available for managing books, library members, and loan transactions.

---

## Project Timeline
 
### Phase 0: Foundations
 
- Built a plain Java/Kotlin **Library Management System** through six OOP phases (encapsulation → inheritance → abstraction → interfaces → polymorphism → composition/statics), deliberately sequenced to mirror how concepts build on each other in real-world systems.
- Covered Kotlin fundamentals in depth (null safety, data classes, scope functions, extension functions, companion objects, coroutines overview) with Dart as the comparison baseline.
- Watched a FreeCodeCamp JPA course independently to cover inheritance strategies, `@MappedSuperClass`, `@Embeddable`/`@EmbeddedId`, derived queries, `@Modifying`, named queries, and Specification-based dynamic predicates.

### Phase 1: Database Layer
 
- Designed the PostgreSQL schema from scratch:
   - **JOINED inheritance** for the member hierarchy (`members` base table, `students` and `faculties` subclass tables)
   - `books` and `loans` tables with UUID primary keys, enums, `CHECK` constraints, and foreign keys
- Annotated all five model classes as JPA entities, working through key concepts: `@Inheritance(JOINED)`, `@PrimaryKeyJoinColumn`, `@Enumerated(STRING)`, `@ManyToOne`, `updatable = false`, correct use of `@Column` for optional fields, and bidirectional relationship pitfalls.

### Phase 2: REST API Layer
 
- Built six REST endpoints across `BookController`, `MemberController`, `LoanController`.
- Designed a custom exception hierarchy: an abstract `MainException` base class with concrete subclasses, handled globally via `@RestControllerAdvice`.
- Introduced DTOs as Java **records**, with dedicated mapper classes (`BookMapper`, `MemberMapper`, `LoanMapper`) to keep entities decoupled from the API surface.
- Added Bean Validation (`@Valid`, `@NotBlank`, `@NotNull`, `@Size`, `@Pattern`) and extended the global exception handler to cover `MethodArgumentNotValidException` and `HttpMessageNotReadableException` for comprehensive error feedback.

### Phase 3: Architecture Refactor
 
- Split a monolithic `LibraryService` into per-entity services (`BookService`, `MemberService`, `LoanService`) plus a `LibraryService` orchestrator for cross-entity operations.
- Changed `DELETE /loans` from path variables to query parameters after a discussion on REST conventions — the endpoint now takes `isbn` and `memberId` as `@RequestParam`s.

### Phase 4: Testing Suite
 
Built out a full four-layer testing strategy, in order of increasing scope:
 
**1. Service-layer unit tests (Mockito)**
Full suites for `LoanServiceTest`, `BookServiceTest`, `MemberServiceTest`, `LibraryServiceTest`. Common bugs caught along the way: mocking the system-under-test instead of its dependency, assertions on the wrong object, and forgetting to verify mock interactions with `verify()` or `ArgumentCaptor` for argument inspection.
 
**2. Controller slice tests (`@WebMvcTest`)**
Three slices — `BookControllerTest`, `MemberControllerTest`, `LoanControllerTest` — with services mocked via `@MockBean`. Introduced a validation-testing pattern: confirming `@Valid` + `GlobalExceptionHandler` produce 400 status with detailed error messages for invalid inputs.
 
**3. Repository integration tests (`@DataJpaTest`)**
- `BookRepositoryTest`, `MemberRepositoryTest`, `LoanRepositoryTest` — each testing only *custom* derived query methods (inherited `JpaRepository` methods like `save()`/`findById()` were deliberately excluded to avoid testing framework code).
- Used `TestEntityManager.persistAndFlush()` for arrange steps, deliberately kept separate from the repository methods under test, to avoid the system-under-test also being responsible for its own test data setup.
- **Database choice mattered here:** `BookRepositoryTest` ran against embedded H2 (safe, since `Book` has no inheritance complexity); `MemberRepositoryTest` and `LoanRepositoryTest` ran against real PostgreSQL in a test container to stress-test JOINED inheritance queries against the actual target dialect.

**4. End-to-end tests (`@SpringBootTest`)**
- Full application context, real controllers → real services → real repositories → real PostgreSQL, no mocking.
- Used `TestRestTemplate` with `RANDOM_PORT` for maximum realism — real HTTP requests over a real embedded Tomcat instance, chosen deliberately over `MockMvc` to stay as close as possible to how actual clients will interact with the API.
- Two end-to-end scenarios:
   - **Full member lifecycle** — register → add book → borrow → return, verifying both HTTP contract (status codes, response bodies) and actual database state at each step.
   - **Multi-book/loan scenario** — register a member, add multiple books, borrow several, and verify state via a mix of real `GET` endpoints and direct repository assertions (since not every endpoint returns full loan history in the response).
- Cleanup handled via `@AfterEach`, explicitly deleting `Loan` rows before `Member`/`Book` rows to respect foreign key dependency order.

### Phase 5: API Documentation

- Integrated **Swagger/OpenAPI 3.0** using Spring Boot's `springdoc-openapi` dependency to auto-generate interactive API documentation.
- Annotated all controllers with `@Tag`, `@Operation`, `@Parameter`, and `@io.swagger.v3.oas.annotations.responses.ApiResponse` to provide rich endpoint descriptions, parameter explanations, and response code examples.
- Configured custom OpenAPI bean with detailed API info (title, version, description, contact, license) to make the Swagger UI both user-friendly and professional.
- Documented all request/response DTOs using `@Schema` annotations on records to expose field descriptions, examples, and validation constraints in the generated schemas.
- Tested the Swagger UI at `http://localhost:8080/swagger-ui.html` to confirm all six endpoints, their parameters, request/response bodies, and enum values appear correctly.
- Added a `/v3/api-docs` endpoint that serves machine-readable OpenAPI JSON for downstream tooling (code generation, mock servers, analytics).

---

### Phase 6: Containerization with Docker

- Created a **Dockerfile** with a multi-stage build strategy:
   - **Build stage:** Maven image compiles and packages the application into a JAR
   - **Runtime stage:** Lean JRE 21 image runs the compiled JAR, reducing final image size and attack surface
- Set up **Docker Compose** to orchestrate:
   - `backend` service — the Spring Boot application, automatically rebuilt and restarted on file changes during development
   - `library_db` service — PostgreSQL 16 with persistent named volume for data durability
   - Health checks ensuring the database is ready before the backend attempts connections
- Created `.env.example` template for configuration management (database credentials, Spring profile, port mappings).
- Documented the full Docker workflow: copying `.env.example` → `.env`, running `docker-compose up --build`, and stopping/cleanup via `docker-compose down` (with optional `-v` to remove volumes and fresh-start the database).
- Key learnings: multi-stage builds reduce image overhead, health checks prevent race conditions between database startup and application initialization, and named volumes persist data across container restarts.
- **Docker Hub image:** https://hub.docker.com/r/phly101/spring_book_app

---

### Phase 7: Open Library Integration

- Introduced **Kotlin** for the first time in the project to explore polyglot development within a single Spring Boot application (92% Java, 7.7% Kotlin).
- Implemented `OpenLibraryClient` using Spring's `RestClient` to fetch book metadata from the **Open Library API**:
   - `fetchIsbnsForQuery(query)` — searches by subject/keyword and returns matching ISBNs
   - `fetchByIsbns(isbns)` — batches ISBN lookups (chunked into 20 per request) to fetch full book data (title, authors, cover images, page counts, publish dates)
- Built `OpenLibraryMapper` to transform Open Library's nested JSON responses into `CreateBookRequest` DTOs, including:
   - Deduplication by title to avoid importing duplicate editions
   - Fallback handling for missing authors (`"Unknown Author"`) and cover images
   - Custom date parsing via `PublishDateFormatter` to normalize Open Library's varied date formats
- Created `OpenLibraryImportRunner` as a **Spring profile-driven batch importer** (activated via `spring.profiles.active=import`):
   - Runs automatically on application startup (implements `CommandLineRunner`)
   - Searches for books matching `"subject:fantasy"` and imports up to 100
   - Reports progress: ISBNs discovered, books fetched, missing entries, and final save count
   - Integrates with existing `BookService` to persist books to the database
- Key learnings: external API integration requires careful error handling for partial responses, batching is critical for large datasets, and polyglot development allows choosing the right language for each layer (Kotlin's null safety and data classes excelled here).

---

### Phase 8: GraphQL Query Layer

- Integrated **Spring GraphQL** (graphql-java) to provide an alternative query interface alongside REST endpoints.
- Defined GraphQL schema in `schema.graphqls`:
   - `type Query` with `memberProfile(memberId: ID!)` query
   - `type MemberProfile` exposing member details (id, name, memberType) and loan history
   - `type LoanHistoryItem` with loan metadata (loanId, bookTitle, bookIsbn, returnDate)
   - `enum MemberType` mapping Java enums to GraphQL
- Implemented `MemberGraphQLResolver` (Kotlin) to resolve the `memberProfile` query:
   - Uses existing `MemberService` and `LoanService` to fetch data
   - Transforms entities to Kotlin data classes (`MemberProfileDto`, `LoanHistoryItemDto`) for clean DTO → GraphQL mapping
   - Leverages Kotlin's concise syntax for data transformation (`loans.map { toLoanHistoryDto(it) }`)
- Configured Spring GraphQL endpoint at `POST /graphql` with interactive explorer at `http://localhost:8080/graphiql`.
- Exposed member profiles with full loan history in a single query, demonstrating GraphQL's strength in avoiding over-fetching and under-fetching compared to REST.
- Key learnings: GraphQL schema-first design pairs well with Kotlin's type system, Spring GraphQL's annotation-based resolvers keep code minimal, and using DTOs between layers ensures clean separation even in query APIs.

---
 
## Key Design Decisions
 
- **Soft-delete for loans** via nullable `returnDate`, preserving history instead of hard-deleting records.
- **JOINED inheritance** for the member hierarchy, chosen deliberately over single-table or table-per-class, and specifically stress-tested against real Postgres in the test suite because of its complexity and dialect-specific quirks.
- **Orchestrator pattern** (`LibraryService`) for operations spanning multiple entities, keeping single-entity services focused and free of cross-cutting concerns.
- **Polyglot language choice** — Java for core domain logic and REST controllers (mature ecosystem, static typing), Kotlin for integrations and query APIs (concise syntax, null safety, data classes).
- **Explicit over implicit in tests** — e.g., choosing `persistAndFlush()` over relying on JPA's implicit auto-flush, and choosing real Postgres over H2 wherever inheritance or dialect-specific behavior mattered.
- **Simplicity first, escalate only when needed** — e.g., choosing `@AfterEach` manual cleanup over more complex strategies (unique per-test data, `@DirtiesContext`, etc.) because it was the simplest approach that worked correctly.
- **Multi-stage Docker builds** to keep container images lean and reduce deployment overhead.
- **Docker Compose for local development** — a single `docker-compose up --build` command replaces manual database setup, configuration management, and port mapping, improving developer experience and onboarding.
- **Profile-driven batch operations** — using Spring profiles (`spring.profiles.active=import`) to separate import logic from normal application flow, keeping the startup process clean.

---
 
## Lessons Learned
 
A few of the harder-won lessons from this project, worth remembering for next time:
 
- **`@PrePersist` only works on methods, not fields.** A field-level `@PrePersist` silently does nothing — always pair it with a dedicated `protected void onCreate()`-style method.
- **`TestRestTemplate` runs on a separate thread than the test method itself.** Because of this, `@Transactional`-based test rollback does *not* clean up data created via real HTTP calls in `@SpringBootTest` — use explicit `@AfterEach` cleanup instead or leverage test database transactions carefully.
- **Positional URL templating is a silent footgun.** `TestRestTemplate.delete(url, args...)` fills `{placeholders}` positionally, not by name — mismatched argument order produces no compiler error, just silent failures or swapped parameters.
- **Spring Boot 4's modularization changed a lot of "it just works" behavior from Spring Boot 3.** `@DataJpaTest`, `@WebMvcTest`, and `TestRestTemplate` all moved into separate, explicitly-declared dependencies — missing these will cause cryptic "annotation not found" errors.
- **Always assert `getBody()` is non-null before chaining field access on it in HTTP response tests** — not just for null-safety, but because it turns a confusing NPE into a clear, informative assertion failure message.
- **Docker Compose health checks are essential for multi-container workflows.** Without them, the application container starts before PostgreSQL is ready, leading to connection timeouts and cryptic startup errors.
- **Environment variable files (`.env`) reduce configuration friction.** Templating a `.env.example` and documenting the setup process ensures new developers can get the entire stack running with a single `docker-compose up --build` command.
- **External API batching and partial responses require defensive parsing.** Open Library doesn't always return data for every ISBN requested, and publish dates come in inconsistent formats — always validate, deduplicate, and provide fallbacks.
- **Kotlin's null safety shines in data transformation.** Using Kotlin for DTOs and mappers catches null-reference bugs at compile time that would slip past Java, especially when working with incomplete external API responses.
- **GraphQL schema-first design keeps resolvers clean.** Defining types upfront in `schema.graphqls` forces you to think about what clients actually need, resulting in simpler resolver implementations and fewer ad-hoc projections.

---

## Roadmap / What's Next

- [x] Database schema + JPA entity modeling
- [x] REST API layer (controllers, DTOs, validation, exception handling)
- [x] Service layer architecture refactor (per-entity services + orchestrator)
- [x] Service-layer unit tests (Mockito)
- [x] Controller slice tests (`@WebMvcTest`)
- [x] Repository integration tests (`@DataJpaTest`)
- [x] End-to-end tests (`@SpringBootTest`)
- [x] API documentation (Swagger/OpenAPI)
- [x] Containerization with Docker & Docker Compose
- [x] Open Library integration (batch book import with external API)
- [x] GraphQL query layer (schema-driven member profile queries)
- [ ] Adding Spring Security (JWT auth, role-based access control)
- [ ] Deploying to AWS EC2 server
- [ ] Performance optimization (caching, query optimization, async batch processing)
