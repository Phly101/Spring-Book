# Spring-Book — Library Management System

A mentored, from-scratch Spring Boot backend project built to learn backend development with Java/Spring Boot, following a "language-first → build-first → framework" learning philosophy. This README documents the project's purpose, architecture, APIs (REST + GraphQL), recent additions (Open Library integration), and the current folder structure.

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
  - [Phase 7: External Data — Open Library Integration](#phase-7-external-data--open-library-integration)
  - [Phase 8: GraphQL API Layer](#phase-8-graphql-api-layer)
- [Key Design Decisions](#key-design-decisions)
- [Lessons Learned](#lessons-learned)
- [Roadmap / What's Next](#roadmap--whats-next)

---
## Motivation & Learning Approach

After attempting several Spring Boot courses and repeatedly feeling lost from jumping in at mid-concept entry points, I settled on an approach that mirrored how I successfully learned Flutter: **learn the language and core concepts first, then build incrementally while introducing libraries/frameworks when they solve a clear problem**.

This project was built through a strict **mentor-only dynamic**: tasks were assigned, code was reviewed, and hints were given — but code was only written *for* me on explicit request. I wrote 99% of the code and used reviews to refine design and to learn idiomatic Spring practices.

Before touching Spring, I built a plain **Java/Kotlin Library Management System** through six OOP phases (encapsulation, inheritance, abstraction, interfaces, polymorphism, composition/statics) to ground the domain model.

---

## Tech Stack

| Layer            | Technology                                                                  |
|------------------|-----------------------------------------------------------------------------|
| Language         | Java 21                                                                     |
| Framework        | Spring Boot 4.x                                                             |
| Persistence      | Spring Data JPA / Hibernate                                                 |
| Database         | PostgreSQL                                                                  |
| Build Tool       | Maven                                                                       |
| Testing          | JUnit 5, Mockito, AssertJ, `@DataJpaTest`, `@WebMvcTest`, `@SpringBootTest` |
| API Docs         | Swagger/OpenAPI 3.0 + GraphQL schema                                        |
| Containerization | Docker & Docker Compose                                                     |
| Security         | Spring Security / JWT (planned)

---

## Architecture Overview

The project follows **Clean Architecture** principles adapted to Spring Boot:

Controller Layer   → REST endpoints, request/response DTOs, validation
Service Layer      → Business logic, split by entity + orchestrator for cross-entity ops
Repository Layer   → Spring Data JPA interfaces, custom derived queries
Model Layer        → JPA entities, JOINED inheritance for the Member hierarchy

**Core entities:** `Book`, `Member` (base class), `Student` / `Faculty` (subclasses via JOINED inheritance), `Loan`.

**Service layer structure:**
- `BookService`, `MemberService`, `LoanService` — own entity-specific logic
- `LibraryService` — orchestrator for cross-entity operations (`borrowBook`, `returnBook`, `getTotalTransactions`)

**Soft-delete pattern:** Loan history is preserved via a nullable `returnDate` column rather than physically deleting loan records — an active loan is one where `returnDate IS NULL`.

## Project Structure

This section reflects the repository layout after recent commits (Open Library integration + GraphQL endpoint additions). Only high-level files and important packages are shown; tests and resources mirror the main layout.

```
library/
   ├── src/
   │   ├── main/
   │   │   ├── java/com/phly101/library/
   │   │   │   ├── controller/                 # REST controllers (Books, Members, Loans)
   │   │   │   │   ├── BookController.java
   │   │   │   │   ├── LoanController.java
   │   │   │   │   └── MemberController.java
   │   │   │   ├── graphql/                    # GraphQL layer (resolvers/controllers, schema wiring)
   │   │   │   │   ├── GraphQLController.java
   │   │   │   │   ├── BookResolver.java
   │   │   │   │   └── schema/                 # .graphqls schema files
   │   │   │   ├── openlibrary/               # External data integration
   │   │   │   │   ├── OpenLibraryClient.java  # Http client for fetching book data
   │   │   │   │   ├── OpenLibraryService.java # Service to map remote data to domain
   │   │   │   │   └── dto/                    # DTOs for remote API responses
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
   │   │   └── resources/
   │   │       ├── application.yml
   │   │       ├── banner.txt
   │   │       └── graphql/                      # GraphQL schema files (.graphqls)
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

**Key directories (updated):**
- `controller/` — REST controllers for Books, Members, and Loans
- `graphql/` — GraphQL controllers/resolvers and schema (.graphqls) files; adds an alternate API layer for querying and mutating domain objects
- `openlibrary/` — Client and service for fetching book metadata from the Open Library API and mapping it into our `Book` domain
- `dto/` — Request/response records organized by entity type
- `exception/` — Custom exception hierarchy and global exception handler
- `mapper/` — Entity ↔ DTO converters
- `model/` — JPA entities and enums
- `repository/` — Spring Data JPA interfaces with custom queries
- `service/` — Business logic organized by entity + orchestrator
- `test/` — Mirrors main codebase structure, includes unit, slice, repository, and end-to-end tests

---

## API Documentation

The API is documented using **Swagger/OpenAPI 3.0** (for REST) and a GraphQL schema for GraphQL queries/mutations.

- Swagger UI (REST): `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`
- GraphQL endpoint: `http://localhost:8080/graphql` (GraphiQL/Altair can be used in development)

### REST Endpoint Coverage (unchanged)

**Books:**
- `POST /books` — Add a new book to the catalog
- `GET /books/{isbn}` — Fetch a specific book by ISBN

**Members:**
- `POST /members` — Register a new library member (Student or Faculty)
- `PUT /members/{id}` — Update member information

**Loans:**
- `POST /loans` — Borrow a book (creates an active loan)
- `DELETE /loans` — Return a book (closes the loan via soft-delete; now accepts `isbn` and `memberId` as `@RequestParam`s)

### GraphQL API (new)

A GraphQL layer was added to provide flexible querying and a single endpoint for complex queries. Highlights:
- Endpoint: `POST /graphql`
- Sample queries:
  - Query book by ISBN, including resolved relations like loan history and current borrower
  - Paginated search for books by title/author
- Mutations:
  - `createBook`, `createMember`, `borrowBook`, `returnBook`
- The GraphQL layer reuses service layer logic (BookService, LoanService, MemberService) and the same DTO/mapping logic where applicable. GraphQL schemas are located under `src/main/resources/graphql/` and mapped via the project's GraphQL configuration.

### Open Library Integration (new)

To enrich book metadata and avoid manual data-entry, the project integrates with the Open Library REST API:
- A dedicated `OpenLibraryClient` fetches book metadata (by ISBN or OLID) and maps responses into domain DTOs.
- `OpenLibraryService` contains logic to transform remote data into `CreateBookRequest` or update existing `Book` entities with richer metadata (cover images, subjects, publishers, publish date).
- Typical flow: developer or an admin calls a sync endpoint (or uses a CLI task) that fetches metadata from Open Library and stores/updates `Book` entries in Postgres.

Notes on implementation:
- The Open Library client uses Spring's `WebClient` with small, testable DTOs that mirror only the fields we need.
- Mapping handles missing fields gracefully and logs mismatches for later inspection.
- Tests include an integration test that stubs the Open Library responses (via WireMock or MockWebServer) to assert mapping correctness.

---

## Project Timeline

### Phase 0: Foundations

- Built a plain Java/Kotlin **Library Management System** through six OOP phases (encapsulation → inheritance → abstraction → interfaces → polymorphism → composition/statics).

### Phase 1: Database Layer

- Designed the PostgreSQL schema from scratch with JOINED inheritance for the member hierarchy, `books` and `loans` tables with UUID primary keys, enums, `CHECK` constraints, and foreign keys.
- Annotated model classes as JPA entities and worked through inheritance mappings and relationship details.

### Phase 2: REST API Layer

- Built six REST endpoints across `BookController`, `MemberController`, `LoanController`.
- Introduced DTO records and mapper classes to decouple entities from API surface.
- Added Bean Validation and a global exception handler.

### Phase 3: Architecture Refactor

- Split monolithic `LibraryService` into per-entity services plus an orchestrator for cross-entity operations.
- Changed `DELETE /loans` to accept `isbn` and `memberId` as query parameters.

### Phase 4: Testing Suite

- Built out service-layer unit tests, controller slice tests (`@WebMvcTest`), repository `@DataJpaTest`s, and `@SpringBootTest` end-to-end tests.

### Phase 5: API Documentation

- Integrated Swagger/OpenAPI 3.0 using `springdoc-openapi` and annotated controllers and DTOs for rich generated docs.

### Phase 6: Containerization with Docker

- Multi-stage Dockerfile and Docker Compose with `library_db` (Postgres) and `backend` service; `.env.example` templating and documented workflows.

### Phase 7: External Data — Open Library Integration (recent)

- Implemented an `OpenLibraryClient` + `OpenLibraryService` to fetch and map metadata from Open Library.
- Added DTOs for the remote API and mapping logic to the local `Book` model.
- Created tests that mock Open Library responses to validate mapping and error handling.
- Benefit: reduces manual metadata entry, provides richer book records (covers, subjects, publishers).

### Phase 8: GraphQL API Layer (recent)

- Added a GraphQL endpoint and schema to provide flexible querying and single-endpoint access for complex object graphs.
- GraphQL resolvers reuse existing service layer logic and mappers; mutations call the same services used by REST controllers.
- GraphQL schema files placed under `src/main/resources/graphql/` and integrated using the project's GraphQL starter configuration.

---

## Key Design Decisions

- **Soft-delete for loans** via nullable `returnDate`, preserving history instead of hard-deleting records.
- **JOINED inheritance** for the member hierarchy, chosen over single-table or table-per-class and tested against real Postgres.
- **Orchestrator pattern** (`LibraryService`) for operations spanning multiple entities.
- **Explicit test behavior** — prefer explicit persistence calls in arrange steps (`persistAndFlush`) and prefer real Postgres for inheritance-sensitive tests.
- **GraphQL adds a flexible read/write layer** but reuses existing service and repository implementations, minimizing duplication.
- **External API client isolation** — Open Library client lives in its own package and is wrapped by a service that contains mapping logic and error handling.

---

## Lessons Learned

- `@PrePersist` only works on methods, not fields.
- `TestRestTemplate` runs on a separate thread than the test method itself; transactional rollbacks don't cover real HTTP calls.
- Positional URL templating with `TestRestTemplate.delete(url, args...)` is a silent footgun.
- Spring Boot 4 modularization changed some testing behavior; explicit dependencies are required for test slices.
- Docker Compose health checks are essential for multi-container workflows.

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
- [x] Open Library metadata integration
- [x] GraphQL API endpoint
- [ ] Adding Spring Security
- [ ] Deploying to AWS EC2 server

---

If you'd like, I can:
- Open a short HOWTO section showing example GraphQL queries and a sample cURL call to the Open Library sync endpoint.
- Add a small example of the `openlibrary` DTO and mapping code to the repo as a starting point for contributors.

