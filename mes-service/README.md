# Manufacturing Execution System (MES)

Controls factory floor production jobs.

## Module Details
- **Port**: `8084`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `ProductionOrder`, `WorkOrder`.
Domain Events: `MaterialConsumptionRequestedEvent`, `ProductionCompletedEvent`.

Tracks the conversion of raw materials into finished furniture. Each `ProductionOrder` contains multiple `WorkOrder`s representing specific machine operations (Cutting, Assembly, Polishing).

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl mes-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
