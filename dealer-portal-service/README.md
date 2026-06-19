# Dealer Portal (B2B)

B2B wholesale ordering portal for retail stores.

## Module Details
- **Port**: `8088`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `WholesaleOrder`, `WholesaleItem`.
Domain Events: `BulkDiscountAppliedEvent`.

Allows third-party retail stores to bulk-order furniture. Integrates directly with `erp-central-service`.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl dealer-portal-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
