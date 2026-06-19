# Common Domain Library

Shared Domain-Driven Design (DDD) base classes.

## Module Details
- **Port**: `N/A (Library)`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Provides foundational DDD patterns such as `AggregateRoot`, `BaseEntity`, and the `DomainEvent` marker interface. All microservices depend on this module to ensure a consistent domain model architecture across the enterprise.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl common-domain
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
