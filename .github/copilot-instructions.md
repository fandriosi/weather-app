# Copilot Instructions for weather (Spring Boot)

## Project Overview
- **Purpose:** Weather station management system built with Spring Boot 3, Java 21, and PostgreSQL.
- **Entry Point:** `src/main/java/com/andriosi/weather/SistemaDeEstacaoMeteorologicaApplication.java`
- **Architecture:**
  - Layered: `domain` (entities), `repository` (data access), `service` (business logic), `web` (controllers), `dto` (data transfer), `config` (configuration), `security` (auth), `storage` (file handling).
  - Uses DTOs for API boundaries and ModelMapper for mapping.
  - Security via JWT (see `app.security.jwt.*` in `application.properties`).
  - File storage supports local and S3 (see `StorageProperties.java` and `application.properties`).
  - Database migrations managed by Flyway (`src/main/resources/db/migration`).

## Developer Workflows
- **Build:** `./mvnw clean package`
- **Run:** `./mvnw spring-boot:run` (or use VS Code task "Run Spring Boot app")
- **Test:** `./mvnw test`
- **Migrations:** `./mvnw flyway:migrate` (uses PostgreSQL, see connection in `application.properties`)
- **Debug:** Attach to port 5005 if enabled in run config (not default).

## Key Conventions & Patterns
- **DTOs:** All API input/output uses DTOs in `web/dto` or `dto`.
- **Mapping:** Use ModelMapper, configured in `config/MapperConfig.java`.
- **Exception Handling:** Centralized in `exeception/GlobalExceptionHandler.java`.
- **Security:**
  - JWT-based, see `security` package and `application.properties` for secrets/expiration.
  - Roles are managed via `Role`, `RoleName`, and `AppUser` in `domain`.
- **File Storage:**
  - Local by default (`storage/sensors/`), S3 supported via config.
  - Storage logic in `storage/` package.
- **Database:**
  - Entities in `domain/`, repositories in `repository/`.
  - Migrations in `src/main/resources/db/migration/`.

## Integration & External Dependencies
- **PostgreSQL:** Main DB, connection in `application.properties`.
- **AWS S3:** Optional for file storage, configured via properties.
- **OpenAPI:** API docs via springdoc-openapi (see `/swagger-ui.html` when running).
- **JWT:** Auth via `io.jsonwebtoken`.

## Examples
- Add a new API: Create DTO, Controller in `web/`, Service in `service/`, map DTOs, update ModelMapper config if needed.
- Add a migration: Place SQL in `src/main/resources/db/migration/`, run Flyway.

## References
- Main config: `src/main/resources/application.properties`
- Build: `pom.xml`
- Entry: `SistemaDeEstacaoMeteorologicaApplication.java`
- Exception: `exeception/GlobalExceptionHandler.java`
- Storage: `storage/`, `StorageProperties.java`

---
For more, see https://aka.ms/vscode-instructions-docs
