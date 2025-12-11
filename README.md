# Patient Management Microservices

> **Status:** 🚧 Under Active Development

A microservices-based patient management system. Currently, the **Patient Service** is implemented.

## Patient Service

RESTful microservice for managing patient records with CRUD operations.

### Tech Stack
- Java 21
- Spring Boot 3.5.8
- Spring Data JPA
- H2 Database (in-memory)
- Lombok
- OpenAPI/Swagger

### Quick Start

```bash
cd patient-service
./mvnw spring-boot:run
```

Server runs on `http://localhost:4000`

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/patients` | Create patient |
| GET | `/api/v1/patients` | Get all patients |
| GET | `/api/v1/patients/{id}` | Get patient by ID |
| PUT | `/api/v1/patients/{id}` | Update patient |
| DELETE | `/api/v1/patients/{id}` | Delete patient |

### API Documentation

Swagger UI: `http://localhost:4000/swagger-ui.html`

### Database

H2 Console: `http://localhost:4000/h2-console`
- **JDBC URL:** `jdbc:h2:mem:testdb`
- **Username:** `sa`
- **Password:** (empty)

Sample data is auto-populated on startup via `data.sql`.

## Roadmap

- [ ] Additional microservices (Appointment, Billing, etc.)
- [ ] Service discovery
- [ ] API Gateway
- [ ] Centralized configuration
- [ ] Authentication & Authorization

## Build & Test

```bash
./mvnw clean install
./mvnw test
```
