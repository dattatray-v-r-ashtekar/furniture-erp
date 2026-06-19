# Procurement Microservice

Handles purchasing raw materials from external vendors.

## Module Details
- **Port**: `8082`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `PurchaseOrder`, `PurchaseOrderLine`.
Domain Events: `PurchaseOrderIssuedEvent`, `GoodsReceivedEvent`.

Responsible for creating purchase orders. Once a vendor delivers the goods, this service emits a `GoodsReceivedEvent`, alerting the `inventory-service` to increase available stock.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl procurement-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
