# Enterprise Architecture

This document outlines the high-level architecture of the Furniture Manufacturing ERP. The system is built on **Domain-Driven Design (DDD)** and **Event-Driven Microservices**.

## High-Level System Architecture Diagram

```mermaid
graph TD
    %% External Actors
    User([Factory Worker / Admin])
    WebUI[Frontend Dashboard\n(Vanilla JS SPA)]
    Gemini([Google Gemini API])

    %% UI to Backend
    User -->|Interacts| WebUI
    WebUI -->|REST API| API_Gateway{Monolith Runner\nor K8s Gateway}

    %% Backend Services (Java)
    subgraph "Core Java Microservices (Spring Boot)"
        API_Gateway --> MES[mes-service]
        API_Gateway --> INV[inventory-service]
        API_Gateway --> PROC[procurement-service]
        API_Gateway --> ERP[erp-central-service]
        API_Gateway --> Others[10 Other Microservices...]
    end

    %% Infrastructure Data Layer
    subgraph "Data & Event Infrastructure (Docker)"
        DB[(PostgreSQL\nShared/Isolated Schemas)]
        Kafka[[Apache Kafka\nEvent Bus]]
        Redis[(Redis\nCache)]
    end

    %% Java to Infra Connections
    MES -->|Reads/Writes| DB
    INV -->|Reads/Writes| DB
    PROC -->|Reads/Writes| DB
    ERP -->|Reads/Writes| DB
    
    MES -->|Publishes/Subscribes Events| Kafka
    INV -->|Publishes/Subscribes Events| Kafka
    PROC -->|Publishes/Subscribes Events| Kafka
    ERP -->|Publishes/Subscribes Events| Kafka

    %% Python AI Service
    subgraph "AI Analytics Layer (Python)"
        AIBrain[ai-analytics-service\nFastAPI + Confluent Kafka]
    end

    %% AI to Infra and External
    Kafka -->|Streams Domain Events| AIBrain
    AIBrain -->|Prompt & Event Data| Gemini
```

## Architecture Explanation

### 1. The Core Microservices (Java 21 + Spring Boot)
The enterprise is split into 14 distinct business domains (e.g., Manufacturing, Inventory, Sales). Each service owns its data strictly and uses Domain-Driven Design principles (`AggregateRoot`, `DomainEvent`).
- **Data Storage**: Services store their state in PostgreSQL. In a cloud deployment, each service would have its own isolated database instance to prevent coupling.
- **Local Monolith Runner**: For local development, all 14 services can be collapsed into a single `erp-monolith-runner` JVM process to save RAM, while still behaving like distributed microservices logically.

### 2. The Event Bus (Apache Kafka)
Because the domains are strictly isolated, they cannot make synchronous, blocking HTTP calls to each other. Instead, they communicate asynchronously.
When the `procurement-service` successfully receives raw wood, it publishes a `GoodsReceivedEvent` to Apache Kafka. The `inventory-service` listens to that topic, receives the event, and increments the wood stock. This ensures maximum fault tolerance (if the inventory service crashes, Kafka holds the message until it reboots).

### 3. The AI Brain (Python + Gemini)
A dedicated Python microservice (`ai-analytics-service`) runs parallel to the Java stack. It acts as a passive observer, constantly listening to the Kafka event stream. When business events occur, it dynamically constructs LLM prompts and queries the Google Gemini API to return actionable business insights, such as predictive maintenance warnings or supply chain optimizations.

### 4. The Frontend Dashboard
A lightweight, lightning-fast Single Page Application built using pure HTML, Vanilla JS, and CSS Glassmorphism styling. It dynamically routes REST API calls directly to the backend services.
