# ERP Central Microservice

Centralized management of customer sales orders.

## Module Details
- **Port**: `8083`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `SalesOrder`, `SalesOrderLine`.
Domain Events: `SalesOrderCreatedEvent`.

The central hub for incoming customer orders (from eCommerce or B2B portals). It orchestrates order fulfillment by communicating with the warehouse and logistics systems.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl erp-central-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
