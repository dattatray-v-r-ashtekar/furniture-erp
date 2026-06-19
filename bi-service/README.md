# Business Intelligence (BI) Service

Generates KPI reports and data dashboards.

## Module Details
- **Port**: `8094`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `DashboardReport`, `KpiMetric`.
Domain Events: `ReportGeneratedEvent`.

Aggregates data across the enterprise (MES, ERP, CRM) to provide management with insights into profitability and production efficiency.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl bi-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
