# Human Resources Management System (HRMS)

Factory and office employee tracking and shift scheduling.

## Module Details
- **Port**: `8090`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `EmployeeRecord`, `AttendanceLog`.
Domain Events: `ShiftAssignedEvent`.

Tracks employee data, factory shift allocations, attendance, and performance.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl hrms-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
