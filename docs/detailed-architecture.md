# Detailed System Architecture

While the high-level architecture diagram shows the conceptual flow, this document provides a comprehensive technical deep-dive into the entire Furniture ERP system.

## 1. High-Level System Architecture Diagram (Expanded)

```mermaid
graph TD
    %% External Actors
    User(["Factory Worker / Admin"])
    WebUI["Frontend Dashboard<br/>(Vanilla JS SPA)"]
    Gemini(["Google Gemini API"])

    %% UI to Backend
    User -->|Interacts| WebUI
    WebUI -->|"REST API"| API_Gateway{"Monolith Runner<br/>or K8s Gateway"}

    %% Backend Services (Java)
    subgraph "Core Java Microservices (Spring Boot)"
        API_Gateway --> M1["inventory-service"]
        API_Gateway --> M2["procurement-service"]
        API_Gateway --> M3["erp-central-service"]
        API_Gateway --> M4["mes-service"]
        API_Gateway --> M5["wms-service"]
        API_Gateway --> M6["tms-service"]
        API_Gateway --> M7["crm-service"]
        API_Gateway --> M8["dealer-portal-service"]
        API_Gateway --> M9["ecommerce-service"]
        API_Gateway --> M10["hrms-service"]
        API_Gateway --> M11["payroll-service"]
        API_Gateway --> M12["accounting-service"]
        API_Gateway --> M13["qms-service"]
        API_Gateway --> M14["bi-service"]
    end

    %% Infrastructure Data Layer
    subgraph "Data & Event Infrastructure (Docker)"
        DB[("PostgreSQL<br/>Shared/Isolated Schemas")]
        Kafka[["Apache Kafka<br/>Event Bus"]]
    end

    %% Connections to DB
    M1 & M2 & M3 & M4 & M5 & M6 & M7 -->|"Reads/Writes"| DB
    M8 & M9 & M10 & M11 & M12 & M13 & M14 -->|"Reads/Writes"| DB
    
    %% Connections to Kafka
    M1 & M2 & M3 & M4 & M5 & M6 & M7 -->|"Publishes/Subscribes Events"| Kafka
    M8 & M9 & M10 & M11 & M12 & M13 & M14 -->|"Publishes/Subscribes Events"| Kafka

    %% Python AI Service
    subgraph "AI Analytics Layer (Python)"
        AIBrain["ai-analytics-service<br/>FastAPI + Confluent Kafka"]
    end

    %% AI to Infra and External
    Kafka -->|"Streams Domain Events"| AIBrain
    AIBrain -->|"Prompt & Event Data"| Gemini
```

## 1. Domain-Driven Design (DDD) Strategy
This ERP is strictly modeled using Domain-Driven Design. Instead of having a single massive database where tables are tangled together with foreign keys, the business is separated into **Bounded Contexts**.

Every microservice represents a Bounded Context. A microservice is the **only** entity allowed to read or write to its specific database tables. If the `mes-service` needs to know about an employee's shift, it cannot join the HR tables. It must consume an event from Kafka or make an API call to the `hrms-service`. This ensures that if the HR database schema changes, it doesn't break the manufacturing systems.

## 2. Comprehensive Service Catalog

The backend is composed of 14 distinct Java Spring Boot Microservices. Below is a detailed breakdown of their responsibilities and domains:

### Manufacturing & Operations
*   **`mes-service` (Manufacturing Execution System)**
    *   **Domain**: Factory floor operations.
    *   **Responsibilities**: Tracks `WorkOrders`, assembly line routing, and machine assignments. It consumes `SalesOrderCreated` events and plans the physical building of the furniture.
*   **`inventory-service` (Warehouse Stock)**
    *   **Domain**: Raw materials (wood, glue, screws) and Finished Goods (sofas, tables).
    *   **Responsibilities**: Maintains exact counts of physical inventory. Consumes events to deduct stock when manufacturing begins, and adds stock when purchasing receives shipments.
*   **`procurement-service` (Purchasing)**
    *   **Domain**: Vendor management and purchasing raw materials.
    *   **Responsibilities**: Issues `PurchaseOrders` to suppliers.
*   **`wms-service` (Warehouse Management System)**
    *   **Domain**: Physical warehouse layout.
    *   **Responsibilities**: Tracks the physical `BinLocation` of inventory. Ensures forklifts know exactly which aisle and shelf a pallet of wood is on.
*   **`qms-service` (Quality Management System)**
    *   **Domain**: Defect tracking and QA.
    *   **Responsibilities**: Schedules inspections for finished goods. Can pause shipments if items fail quality checks.

### Sales & Customers
*   **`erp-central-service` (Order Management)**
    *   **Domain**: The central nervous system for sales.
    *   **Responsibilities**: Converts leads and portal orders into official `SalesOrders`.
*   **`dealer-portal-service` (B2B)**
    *   **Domain**: Wholesale.
    *   **Responsibilities**: Provides a REST API for retail partners (like IKEA or Wayfair) to submit bulk orders.
*   **`ecommerce-service` (B2C)**
    *   **Domain**: Direct to consumer.
    *   **Responsibilities**: Handles single-item purchases from the public website, managing shopping carts and payment gateway integrations.
*   **`crm-service` (Customer Relationship)**
    *   **Domain**: Leads and support.
    *   **Responsibilities**: Tracks potential B2B clients, support tickets, and sales rep performance.

### Logistics & Finance
*   **`tms-service` (Transportation Management System)**
    *   **Domain**: Shipping and logistics.
    *   **Responsibilities**: Tracks truck fleets, calculates delivery routes, and monitors fuel usage.
*   **`accounting-service` (Finance)**
    *   **Domain**: General Ledger.
    *   **Responsibilities**: Manages Accounts Payable (paying vendors) and Accounts Receivable (collecting from dealers). It listens to almost all Kafka events to record financial impacts automatically.
*   **`bi-service` (Business Intelligence)**
    *   **Domain**: Data Warehousing.
    *   **Responsibilities**: A specialized service that aggregates data from Kafka to build historical reports (e.g., quarterly revenue, factory efficiency).

### Human Resources
*   **`hrms-service` (HR Management)**
    *   **Domain**: Employee records.
    *   **Responsibilities**: Manages employee onboarding, roles, and shift scheduling.
*   **`payroll-service` (Payroll)**
    *   **Domain**: Compensation.
    *   **Responsibilities**: Calculates salary, hourly wages for factory workers, and tax deductions.

---

## 3. The Shared Data Architecture

```mermaid
graph LR
    subgraph "PostgreSQL Instance (Docker)"
        S1[("schema: inventory_db")]
        S2[("schema: procurement_db")]
        S3[("schema: erp_central_db")]
        S4[("schema: mes_db")]
        S5[("schema: wms_db")]
        S6[("schema: tms_db")]
        S7[("schema: crm_db")]
        S8[("schema: dealer_portal_db")]
        S9[("schema: ecommerce_db")]
        S10[("schema: hrms_db")]
        S11[("schema: payroll_db")]
        S12[("schema: accounting_db")]
        S13[("schema: qms_db")]
        S14[("schema: bi_db")]
    end

    M1["inventory-service"] -->|"JDBC"| S1
    M2["procurement-service"] -->|"JDBC"| S2
    M3["erp-central-service"] -->|"JDBC"| S3
    M4["mes-service"] -->|"JDBC"| S4
    M5["wms-service"] -->|"JDBC"| S5
    M6["tms-service"] -->|"JDBC"| S6
    M7["crm-service"] -->|"JDBC"| S7
    M8["dealer-portal-service"] -->|"JDBC"| S8
    M9["ecommerce-service"] -->|"JDBC"| S9
    M10["hrms-service"] -->|"JDBC"| S10
    M11["payroll-service"] -->|"JDBC"| S11
    M12["accounting-service"] -->|"JDBC"| S12
    M13["qms-service"] -->|"JDBC"| S13
    M14["bi-service"] -->|"JDBC"| S14
    
    %% Denoting isolation example
    M1 -.->|"Access Denied"| S4
```

We utilize a **Database-per-Service** pattern (implemented via isolated schemas within a single Postgres instance to save costs). This guarantees loose coupling.

---

## 4. Comprehensive Event Flow Example

Here is a highly detailed look at how these 14 services interact using Kafka during a complex scenario: **A Bulk Order Fulfillment**.

```mermaid
sequenceDiagram
    autonumber
    participant DEALER as dealer-portal-service
    participant ERP as erp-central-service
    participant MES as mes-service
    participant INV as inventory-service
    participant TMS as tms-service
    participant ACC as accounting-service
    participant Kafka as Apache Kafka

    Note over DEALER, Kafka: 1. A retail partner orders 500 Sofas
    DEALER->>Kafka: Publish [B2BOrderSubmitted]
    
    Kafka->>ERP: Consume
    ERP->>ERP: Validate Pricing & Create SalesOrder
    ERP->>Kafka: Publish [SalesOrderCreated]
    
    par Parallel Processing
        Kafka->>MES: Consume
        MES->>MES: Generate 500 Assembly WorkOrders
        
        Kafka->>ACC: Consume
        ACC->>ACC: Generate Invoice (Accounts Receivable)
        
        Kafka->>INV: Consume
        INV->>INV: Allocate existing Sofa stock (if any)
    end
    
    Note over MES: Factory workers build the 500 sofas...
    
    MES->>Kafka: Publish [ProductionCompleted]
    
    Kafka->>INV: Consume
    INV->>INV: Add 500 Sofas to Stock
    INV->>Kafka: Publish [InventoryAllocatedToOrder]
    
    Kafka->>TMS: Consume
    TMS->>TMS: Schedule Truck Fleet for Delivery
```

## 5. Security & Gateway Layer (Keycloak)
Security is handled centrally by **Keycloak** (OpenID Connect / OAuth2).
1. When a user logs into the Frontend SPA, they authenticate with the Keycloak Docker container.
2. Keycloak issues a JWT (JSON Web Token).
3. The Frontend attaches this JWT as a `Bearer` token to all REST API calls.
4. The Spring Boot microservices intercept the request, validate the cryptographic signature of the JWT, and extract the user's roles (e.g., `ROLE_FACTORY_MANAGER`, `ROLE_ACCOUNTANT`) before allowing the action.
