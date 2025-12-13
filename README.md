# Patient Management Microservices

> **Status:** 🚧 Under Active Development

A Spring Boot microservices architecture demonstrating gRPC inter-service communication for healthcare management.

## Architecture

```
Patient Service (REST) ──[gRPC]──> Billing Service
       ↓
PostgreSQL Database
```

## Services

### 1. Patient Service
REST API for patient record management with automated billing account creation via gRPC.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Spring Data JPA • PostgreSQL • gRPC Client • OpenAPI

**Port:** `4000`

**Features:**
- Full CRUD operations for patient records
- Automatic billing account creation via gRPC on patient creation
- Bean validation with custom validation groups
- Auto-populated sample data (15 patients)
- OpenAPI/Swagger documentation

**API Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/patients` | Create patient + billing account |
| GET | `/api/v1/patients` | Get all patients |
| GET | `/api/v1/patients/{id}` | Get patient by ID |
| PUT | `/api/v1/patients/{id}` | Update patient |
| DELETE | `/api/v1/patients/{id}` | Delete patient |

### 2. Billing Service
gRPC microservice for billing account creation.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • gRPC Server • Protocol Buffers

**Port:** `4001` (HTTP) • `9001` (gRPC)

**Features:**
- gRPC service endpoint for billing account creation
- Protocol Buffers for type-safe service contracts

## Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven 3.9+

### 1. Start Database
```bash
docker-compose up -d
```

### 2. Start Services
```bash
# Terminal 1 - Billing Service
cd billing-service
./mvnw spring-boot:run

# Terminal 2 - Patient Service
cd patient-service
./mvnw spring-boot:run
```

### 3. Access API Documentation
- Swagger UI: http://localhost:4000/swagger-ui.html
- OpenAPI Spec: http://localhost:4000/v3/api-docs

## Database

**PostgreSQL:** `localhost:5432`
- Database: `patient_service`
- User: `root`
- Password: `password`

### Auto-Populated Sample Data

The database automatically populates with 15 sample patients on application startup via `data.sql`.

**How it works:**
1. `spring.sql.init.mode: always` in `application.yml` enables SQL script execution
2. `data.sql` in `src/main/resources` contains INSERT statements with predefined UUIDs
3. Uses `WHERE NOT EXISTS` to prevent duplicate entries on restart
4. Hibernate's `ddl-auto: update` creates tables, then SQL scripts populate data

**To disable auto-population:** Set `spring.sql.init.mode: never` in `application.yml`

## Build

```bash
# Build both services
cd patient-service && ./mvnw clean install
cd ../billing-service && ./mvnw clean install
```

## What's Implemented

✅ Patient Service REST API with full CRUD  
✅ PostgreSQL database integration  
✅ gRPC client-server communication  
✅ Protocol Buffers for service contracts  
✅ Bean validation with custom groups  
✅ Auto-populated sample data  
✅ OpenAPI/Swagger documentation  
✅ Docker Compose for PostgreSQL  
✅ Exception handling and custom exceptions  

## Planned Features

🚧 Service Discovery (Eureka)  
🚧 API Gateway  
🚧 Appointment Service  
🚧 Authentication & Authorization  
🚧 Distributed Tracing  
🚧 Centralized Configuration

