# Spring-Book — Library Management System

A mentored, from-scratch Spring Boot backend project built to learn backend development with Java/Spring Boot. This README documents the project's purpose, architecture, APIs, and the current folder structure (Java + Kotlin sources).

---

## Table of Contents

- [Tech Stack](#tech-stack)
- [Architecture Overview](#architecture-overview)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)
- [Project Timeline](#project-timeline)
- [Roadmap / What's Next](#roadmap--whats-next)

---

## Tech Stack

| Layer            | Technology                                  |
|------------------|---------------------------------------------|
| Language         | Java 21 + Kotlin                             |
| Framework        | Spring Boot 4.x                              |
| Persistence      | Spring Data JPA / Hibernate                  |
| Database         | PostgreSQL                                   |
| Build Tool       | Maven (wrapper included in `library/`)       |
| Containerization | Docker & Docker Compose                      |

---

## Architecture Overview

Clean Architecture adapted to Spring Boot:
- Controller layer (HTTP API)
- Service layer (business logic)
- Repository layer (Spring Data)
- Model layer (JPA entities)

Some integration and DTO code is implemented in Kotlin (see Project Structure).

## Project Structure

Below is a concise, accurate view of the repository layout. I only list the important packages/files as requested (controllers, repositories, resources, and the Kotlin integration/DTO locations).

```
<repo root>
  ├── .dockerignore
  ├── .env.example
  ├── .gitignore
  ├── Dockerfile
  ├── docker-compose.yaml
  ├── README.md
  └── library/                # runnable Maven module
      ├── mvnw, mvnw.cmd
      ├── pom.xml
      ├── .gitattributes
      ├── src/
      │   ├── main/
      │   │   ├── java/com/phly101/library/
      │   │   │   ├── controller/     # REST controllers (BookController, MemberController, LoanController)
      │   │   │   ├── repository/     # Spring Data repositories
      │   │   │   ├── service/        # Business logic and orchestrator services
      │   │   │   ├── model/          # JPA entities & enums
      │   │   │   ├── dto/            # Java DTOs (request/response records)
      │   │   │   ├── mapper/         # Entity ↔ DTO mappers
      │   │   │   ├── exception/      # Exceptions + handlers
      │   │   │   └── LibraryApplication.java
      │   │   ├── kotlin/com/phly101/library/
      │   │   │   ├── integration/openlibrary/   # Open Library client, mapper, import runner (Kotlin)
      │   │   │   └── dto/                       # Kotlin DTOs used by GraphQL and integrations
      │   │   └── resources/
      │   │       ├── application.yml
      │   │       └── banner.txt
      │   └── test/             # Unit, slice, repository and E2E tests (mirror of main)
      ├── Dockerfile
      └── README.md
```

Notes:
- The runnable module is the `library/` folder (it contains `pom.xml`, the Maven wrapper, and the application entry).
- Java sources (controllers, repositories, services, entities) are under `library/src/main/java/com/phly101/library/`.
- Kotlin sources live under `library/src/main/kotlin/com/phly101/library/` and currently contain:
  - `integration/openlibrary/` — Open Library client, DTOs and mapping logic implemented in Kotlin
  - `dto/` — Kotlin DTOs that the GraphQL layer (and some integration code) use
- I verified `OpenLibraryClient.kt`, `OpenLibraryMapper.kt`, `OpenLibraryImportRunner.kt`, and several DTOs under `integration/openlibrary/dto/` exist in the Kotlin sources.

---

## API Documentation

- Swagger UI (REST): `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

If you want README examples for the Open Library import runner or GraphQL DTO usage, I can add concise HOWTO snippets (Kotlin + cURL examples).

---

## Roadmap / What's Next

- [ ] Add brief HOWTO examples for: Open Library import runner; GraphQL DTOs usage
- [ ] Add a short developer guide on where to add new REST endpoints vs Kotlin-based integrations

---

If this matches your repo, I will commit the README update. Would you like me to commit directly to main or open a PR for review?
