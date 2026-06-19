# Quality Management System (QMS)

Tracks defects during and after production.

## Module Details
- **Port**: `8093`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `QualityInspection`, `DefectLog`.
Domain Events: `InspectionFailedEvent`.

Works closely with `mes-service` to log defective furniture builds and perform root-cause analysis on the factory floor.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl qms-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
