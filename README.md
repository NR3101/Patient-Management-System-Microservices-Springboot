# Patient Management Microservices

> **Status:** 🚧 Under Active Development

A Spring Boot microservices architecture demonstrating JWT authentication, gRPC inter-service communication, and event-driven design for healthcare management.

## Architecture

```
                    API Gateway (4004)
                          ↓
         ┌────────────────┼────────────────┐
         ↓                ↓                ↓
   Auth Service    Patient Service   Analytics Service
      (4003)           (4000)             (4002)
         ↓                ↓                   ↑
   PostgreSQL       ├─────┴─────┐            │
    (5433)          ↓           ↓            │
              PostgreSQL   Billing Service ──┘
               (5432)          (4001)   Kafka Events
                                (gRPC: 9001)
```

## Services

### 1. Auth Service
JWT-based authentication and token validation service.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Spring Security • JWT (jjwt 0.12.6) • PostgreSQL • OpenAPI

**Port:** `4003`

**Features:**
- JWT token generation on successful login
- Token validation endpoint for other services
- BCrypt password hashing
- Auto-populated test user on startup
- Stateless authentication (no sessions)
- OpenAPI/Swagger documentation

**API Endpoints:**
| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/auth/login` | Login and get JWT token |
| GET | `/api/v1/auth/validate` | Validate JWT token |

**Test User:**
- Email: `testuser@test.com`
- Password: `password123`
- Role: `ADMIN`

### 2. Patient Service
REST API for patient record management with automated billing account creation via gRPC and event streaming.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Spring Data JPA • PostgreSQL • gRPC Client • Kafka Producer • OpenAPI

**Port:** `4000`

**Features:**
- Full CRUD operations for patient records
- Automatic billing account creation via gRPC on patient creation
- Event publishing to Kafka on patient creation (using Protocol Buffers)
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

### 3. Billing Service
gRPC microservice for billing account creation.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • gRPC Server • Protocol Buffers

**Port:** `4001` (HTTP) • `9001` (gRPC)

**Features:**
- gRPC service endpoint for billing account creation
- Protocol Buffers for type-safe service contracts

### 4. Analytics Service
Kafka consumer microservice for processing patient events and analytics.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Kafka Consumer • Protocol Buffers

**Port:** `4002`

**Features:**
- Consumes patient events from Kafka `patient` topic
- Protocol Buffers deserialization for type-safe event processing
- Consumer group: `analytics-service`
- Real-time event processing and logging

### 5. API Gateway
Spring Cloud Gateway for routing, JWT validation, and centralizing API access with cleaner URL paths.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Spring Cloud Gateway (WebFlux) • WebClient

**Port:** `4004`

**Features:**
- Centralized entry point for all microservices
- **JWT validation filter** for protected routes (auto-validates with Auth Service)
- Cleaner public URLs (removes `/api/v1` prefix at gateway level)
- Path rewriting with `PrefixPath` filter for backend services
- Custom exception handling for unauthorized requests
- API documentation aggregation
- Reactive gateway using Spring WebFlux

**Routes:**
| Gateway Path | Target Service | Backend Path | JWT Protected |
|--------------|----------------|--------------|---------------|
| `/auth/**` | Auth Service | `/api/v1/auth/**` | ❌ |
| `/patients/**` | Patient Service | `/api/v1/patients/**` | ✅ |
| `/api-docs/auth` | Auth API Docs | `/v3/api-docs` | ❌ |
| `/api-docs/patients` | Patient API Docs | `/v3/api-docs` | ❌ |

## Quick Start

### Prerequisites
- Java 21
- Docker & Docker Compose
- Maven 3.9+

### 1. Start Infrastructure (Database + Kafka)
```bash
# Start PostgreSQL and Kafka in KRaft mode
docker-compose up -d

# Wait ~30 seconds for services to be ready
# Verify both containers are running
docker-compose ps
```

### 2. Start Services
```bash
# Terminal 1 - Auth Service
cd auth-service
./mvnw spring-boot:run

# Terminal 2 - Billing Service (Start before Patient Service!)
cd billing-service
./mvnw spring-boot:run

# Terminal 3 - Patient Service
cd patient-service
./mvnw spring-boot:run

# Terminal 4 - Analytics Service (Kafka Consumer)
cd analytics-service
./mvnw spring-boot:run

# Terminal 5 - API Gateway
cd api-gateway
./mvnw spring-boot:run
```

### 3. Access API
**Via API Gateway (Recommended):**
- Auth Service: http://localhost:4004/auth
- Patient Service: http://localhost:4004/patients (requires JWT token)
- Auth API Docs: http://localhost:4004/api-docs/auth
- Patient API Docs: http://localhost:4004/api-docs/patients

**Direct Service Access:**
- Auth Service: http://localhost:4003/api/v1/auth
- Auth Swagger UI: http://localhost:4003/swagger-ui.html
- Patient Service: http://localhost:4000/api/v1/patients
- Patient Swagger UI: http://localhost:4000/swagger-ui.html

### 4. Test the Complete Flow

**Step 1: Authenticate and get JWT token**
```bash
curl -X POST http://localhost:4004/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "testuser@test.com",
    "password": "password123"
  }'
```
Response: `{"token":"eyJhbGciOiJIUzI1NiIsInR5cCI6..."}`

**Step 2: Create a patient via API Gateway (JWT validated automatically)**
```bash
curl -X POST http://localhost:4004/patients \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main St",
    "dateOfBirth": "1990-01-01"
  }'
```

**Step 3: Get all patients (JWT required)**
```bash
curl -X GET http://localhost:4004/patients \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

**Event Flow:**
1. API Gateway validates JWT token with Auth Service
2. API Gateway routes request to Patient Service
3. Patient saved to PostgreSQL (Patient DB)
4. Patient Service calls Billing Service via gRPC
5. Patient Service publishes `PatientEvent` to Kafka
6. Analytics Service consumes and logs the event

## Infrastructure

### PostgreSQL Databases
**Patient Service Database:**
- Connection: `localhost:5432`
- Database: `patient_service`
- User: `root`
- Password: `password`
- Container: `postgres-patient`

**Auth Service Database:**
- Connection: `localhost:5433`
- Database: `auth_service`
- User: `root`
- Password: `password`
- Container: `postgres-auth`

### Kafka Message Broker
**Connection:** `localhost:9094` (external), `kafka:9092` (internal)
- Mode: KRaft (no Zookeeper required)
- Topics: Auto-created on first use (`patient` topic)
- Producer: Patient Service publishes `PatientEvent` messages
- Consumer: Analytics Service consumes from `patient` topic

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
# Build all services
cd auth-service && ./mvnw clean install
cd ../patient-service && ./mvnw clean install
cd ../billing-service && ./mvnw clean install
cd ../analytics-service && ./mvnw clean install
cd ../api-gateway && ./mvnw clean install
```

## What's Implemented

✅ **Authentication & Authorization**: JWT-based auth service with login and token validation  
✅ **Patient Service**: REST API with full CRUD operations  
✅ **Database**: Separate PostgreSQL databases for Auth and Patient services  
✅ **gRPC Communication**: Patient → Billing service inter-service calls  
✅ **Event Streaming**: Kafka producer (Patient) and consumer (Analytics)  
✅ **API Gateway**: Spring Cloud Gateway with centralized routing and cleaner URLs  
✅ **JWT Validation Filter**: Automatic token validation at gateway level for protected routes  
✅ **Path Rewriting**: `PrefixPath` filter to expose clean URLs (`/patients` → `/api/v1/patients`)  
✅ **Protocol Buffers**: Type-safe serialization for gRPC and Kafka  
✅ **Security**: BCrypt password hashing and stateless JWT authentication  
✅ **Validation**: Bean validation with custom validation groups  
✅ **Sample Data**: Auto-populated test users and patients  
✅ **Documentation**: OpenAPI/Swagger for all REST services  
✅ **Infrastructure**: Docker Compose with PostgreSQL + Kafka (KRaft mode)  
✅ **Error Handling**: Custom exceptions and global exception handlers  

## Planned Features

🚧 Service Discovery (Eureka)  
🚧 Load Balancing in API Gateway  
🚧 Appointment Service  
🚧 Distributed Tracing (Zipkin)  
🚧 Centralized Configuration (Spring Cloud Config)  
🚧 Circuit Breaker (Resilience4j)  
🚧 Rate Limiting & Throttling

