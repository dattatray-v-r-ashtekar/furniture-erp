# Transportation Management System (TMS)

Plans multi-stop logistics and shipping routes.

## Module Details
- **Port**: `8086`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `DeliveryRoute`, `DeliveryStop`.
Domain Events: `RouteStartedEvent`.

Coordinates fleet vehicles to deliver finished furniture to retail dealers or directly to consumers.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl tms-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
