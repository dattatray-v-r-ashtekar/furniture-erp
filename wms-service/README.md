# Warehouse Management System (WMS)

Manages physical storage locations and bin capacities.

## Module Details
- **Port**: `8085`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `WarehouseBin`, `BinMovement`.
Domain Events: `BinCapacityReachedEvent`.

Handles the physical put-away and picking of inventory. While `inventory-service` tracks total quantities, WMS tracks the exact aisle, rack, and shelf where items are physically stored.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl wms-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
