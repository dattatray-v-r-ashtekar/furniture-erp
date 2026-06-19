# Customer Relationship Management (CRM)

Manages leads, follow-ups, and interaction history.

## Module Details
- **Port**: `8087`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `CustomerProfile`, `InteractionLog`.
Domain Events: `CustomerConvertedEvent`.

Used by the sales team to track potential leads and their journey toward becoming paying customers.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl crm-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
