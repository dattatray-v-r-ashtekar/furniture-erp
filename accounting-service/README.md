# Accounting Microservice

General ledger and financial tracking.

## Module Details
- **Port**: `8092`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

Core Entities: `GeneralLedger`, `JournalEntry`.
Domain Events: `LedgerBalancedEvent`.

Manages Accounts Payable (from `procurement-service`), Accounts Receivable (from `erp-central-service`), and general financial health.

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl accounting-service
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
