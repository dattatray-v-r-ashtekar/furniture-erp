# Furniture Manufacturing Enterprise Resource Planning (ERP)

A state-of-the-art Enterprise Resource Planning system tailored for a furniture manufacturing business. This system handles B2B (wholesale) and B2C (retail) sales, along with manufacturing, warehouse management, logistics, employees, procurement, and finance.

## Architecture Overview

This project is built using a **Domain-Driven Design (DDD)** and **Event-Driven Microservices** architecture. 
Because manufacturing, warehouse, logistics, and sales are naturally separate business domains that evolve independently, this architecture ensures the system scales easily from a single factory to multiple factories, warehouses, and dealer networks without requiring major redesigns.

### Technology Stack
- **Language**: Java 21
- **Framework**: Spring Boot 3.3.0
- **Database**: PostgreSQL (Shared/Dedicated schemas per service)
- **Messaging/Event Bus**: Apache Kafka & Zookeeper
- **Caching**: Redis
- **Security**: Keycloak
- **Containerization**: Docker & Docker Compose
- **Build Tool**: Maven (Multi-module Monorepo)

*(Future Kubernetes/EKS, NGINX Ingress, ArgoCD, Prometheus, Grafana, ELK, OpenTelemetry, GitHub Actions, and SonarQube integrations are intended for the deployment pipeline).*

---

## System Modules

The project consists of 14 core microservices and 2 shared libraries within a single Maven Monorepo:

### Shared Libraries
1. `common-domain`: Contains the base Domain-Driven Design classes (`AggregateRoot`, `BaseEntity`, `DomainEvent`).
2. `common-messaging`: Contains the Kafka interfaces and publishers for cross-service communication.

### Core Microservices

| Module | Port | Description |
| :--- | :--- | :--- |
| `inventory-service` | `8081` | Manages raw material and finished goods stock. |
| `procurement-service` | `8082` | Purchases raw materials from external vendors. |
| `erp-central-service` | `8083` | Central management of customer sales orders. |
| `mes-service` | `8084` | Manufacturing Execution: Factory floor production jobs. |
| `wms-service` | `8085` | Warehouse location tracking and physical bins. |
| `tms-service` | `8086` | Transportation Management: Logistics and shipping routes. |
| `crm-service` | `8087` | Customer Relationship Management: Leads and follow-ups. |
| `dealer-portal-service`| `8088` | B2B ordering portal for retail stores and wholesalers. |
| `ecommerce-service` | `8089` | B2C direct-to-consumer online sales. |
| `hrms-service` | `8090` | Human Resources: Employee records and shift tracking. |
| `payroll-service` | `8091` | Salary and tax records. |
| `accounting-service` | `8092` | General ledger financial entries (AP/AR). |
| `qms-service` | `8093` | Quality Management: Defect tracking and inspection. |
| `bi-service` | `8094` | Business Intelligence: KPI reports and dashboards. |

---

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.8+
- Docker & Docker Compose

### 1. Start the Infrastructure
The system relies on a local infrastructure stack (PostgreSQL, Kafka, Redis, Keycloak). Start it using Docker Compose:

```bash
# From the root directory
docker-compose up -d
```
*Note: Wait a few moments for Kafka and Postgres to fully initialize.*

### 2. Build the Project
Compile the entire 14-service monorepo using Maven:

```bash
# Skip tests for a faster build
mvn clean install -DskipTests
```

### 3. Run a Microservice
You can run any of the microservices individually using the Spring Boot Maven plugin. They will automatically connect to the local Postgres database (`erp_db`) and the Kafka broker on `localhost:9092`.

```bash
# Example: Running the Inventory Service
mvn spring-boot:run -pl inventory-service

# Example: Running the Manufacturing Execution System (MES)
mvn spring-boot:run -pl mes-service
```

---

## Domain-Driven Design & Event-Driven Workflows

Each microservice is entirely self-contained and communicates with other services asynchronously via Kafka domain events.

For example, the Manufacturing Execution flow:
1. `mes-service` starts a `WorkOrder` and fires a `MaterialConsumptionRequestedEvent`.
2. `inventory-service` (in the future) listens to this event to deduct raw materials (e.g., wood) from the database.
3. Once the furniture is built, `mes-service` fires a `ProductionCompletedEvent`.
4. `inventory-service` listens to this event to automatically add the finished good to the stock.

By leveraging Kafka for these transitions, we guarantee that the warehouse, factory, and purchasing teams' systems operate smoothly and without blocking each other.
