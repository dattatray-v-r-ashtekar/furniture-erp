# Inventory Microservice

Manages raw material and finished goods stock levels.

## Module Details
- **Port**: `8081`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `StockItem`.

Responsible for holding stock quantities of raw materials (like wood, glue, fabric) and finished furniture goods. It listens to external events (like GoodsReceived or ProductionCompleted) to adjust stock levels accordingly.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl inventory-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
