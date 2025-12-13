# Patient Management Microservices

> **Status:** 🚧 Under Active Development

A Spring Boot microservices architecture demonstrating gRPC inter-service communication for healthcare management.

## Architecture

```
                    API Gateway (4004)
                          ↓
              ┌───────────┴───────────┐
              ↓                       ↓
     Patient Service          Analytics Service
          (4000)                   (4002)
              ↓                       ↑
        ├─────┴─────┐                │
        ↓           ↓                │
   PostgreSQL   Billing Service ────┘
                   (4001)      Kafka Events
```

## Services

### 1. Patient Service
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

### 2. Billing Service
gRPC microservice for billing account creation.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • gRPC Server • Protocol Buffers

**Port:** `4001` (HTTP) • `9001` (gRPC)

**Features:**
- gRPC service endpoint for billing account creation
- Protocol Buffers for type-safe service contracts

### 3. Analytics Service
Kafka consumer microservice for processing patient events and analytics.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Kafka Consumer • Protocol Buffers

**Port:** `4002`

**Features:**
- Consumes patient events from Kafka `patient` topic
- Protocol Buffers deserialization for type-safe event processing
- Consumer group: `analytics-service`
- Real-time event processing and logging

### 4. API Gateway
Spring Cloud Gateway for routing and centralizing API access.

**Tech Stack:** Java 21 • Spring Boot 3.5.8 • Spring Cloud Gateway (WebFlux)

**Port:** `4004`

**Features:**
- Centralized entry point for all microservices
- Route configuration for Patient Service
- Path-based routing (`/api/v1/patients/**`)
- API documentation aggregation
- Reactive gateway using Spring WebFlux

**Routes:**
| Path | Target Service | Port |
|------|---------------|------|
| `/api/v1/patients/**` | Patient Service | 4000 |
| `/api-docs/patients` | Patient API Docs | 4000 |

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
# Terminal 1 - Billing Service (Start First!)
cd billing-service
./mvnw spring-boot:run

# Terminal 2 - Patient Service
cd patient-service
./mvnw spring-boot:run

# Terminal 3 - Analytics Service (Kafka Consumer)
cd analytics-service
./mvnw spring-boot:run

# Terminal 4 - API Gateway
cd api-gateway
./mvnw spring-boot:run
```

### 3. Access API
**Via API Gateway (Recommended):**
- API Gateway: http://localhost:4004/api/v1/patients
- Patient Service: http://localhost:4004/api/v1/patients
- API Docs: http://localhost:4004/api-docs/patients

**Direct Service Access:**
- Patient Service: http://localhost:4000/api/v1/patients
- Swagger UI: http://localhost:4000/swagger-ui.html
- OpenAPI Spec: http://localhost:4000/v3/api-docs

### 4. Test the Complete Flow
Create a patient via API Gateway:
```bash
curl -X POST http://localhost:4004/api/v1/patients \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "address": "123 Main St",
    "dateOfBirth": "1990-01-01"
  }'
```

**Event Flow:**
1. API Gateway routes request to Patient Service
2. Patient saved to PostgreSQL
3. Patient Service calls Billing Service via gRPC
4. Patient Service publishes `PatientEvent` to Kafka
5. Analytics Service consumes and logs the event

## Infrastructure

### PostgreSQL Database
**Connection:** `localhost:5432`
- Database: `patient_service`
- User: `root`
- Password: `password`

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
cd patient-service && ./mvnw clean install
cd ../billing-service && ./mvnw clean install
cd ../analytics-service && ./mvnw clean install
cd ../api-gateway && ./mvnw clean install
```

## What's Implemented

✅ Patient Service REST API with full CRUD  
✅ PostgreSQL database integration  
✅ gRPC client-server communication (Patient → Billing)  
✅ Kafka event streaming (async patient events)  
✅ Kafka Consumer service (Analytics Service)  
✅ API Gateway with Spring Cloud Gateway (centralized routing)  
✅ Protocol Buffers for gRPC and Kafka serialization  
✅ Bean validation with custom groups  
✅ Auto-populated sample data (15 patients)  
✅ OpenAPI/Swagger documentation  
✅ Docker Compose with PostgreSQL + Kafka (KRaft mode)  
✅ Exception handling and custom exceptions  

## Planned Features

🚧 Service Discovery (Eureka)  
🚧 Load Balancing in API Gateway  
🚧 Appointment Service  
🚧 Authentication & Authorization (JWT)  
🚧 Distributed Tracing (Zipkin)  
🚧 Centralized Configuration (Spring Cloud Config)  
🚧 Circuit Breaker (Resilience4j)

