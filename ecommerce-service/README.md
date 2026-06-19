# E-Commerce System (B2C)

B2C direct-to-consumer online sales storefront.

## Module Details
- **Port**: `8089`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `OnlineOrder`, `CartItem`.
Domain Events: `PaymentProcessedEvent`.

Handles the consumer-facing shopping cart, checkout, and payment processing before submitting orders to `erp-central-service`.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl ecommerce-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
