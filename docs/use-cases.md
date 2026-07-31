# Business Use Cases & Flows

Because the Furniture ERP is built on an Event-Driven Architecture, complex business processes are choreographed asynchronously. Below are the flow diagrams for critical business use cases.

## 1. The Manufacturing Execution Flow

**Scenario**: A factory manager decides to manufacture a batch of wooden chairs.

```mermaid
sequenceDiagram
    autonumber
    actor FactoryManager as Factory Manager
    participant UI as Frontend Dashboard
    participant MES as mes-service
    participant Kafka as Apache Kafka
    participant INV as inventory-service
    participant AI as ai-analytics-service
    participant Gemini as Google Gemini API

    FactoryManager->>UI: Create Production Order (Chairs)
    UI->>MES: POST /api/v1/mes/orders
    MES-->>UI: 201 Created (Order ID)
    
    MES->>Kafka: Publish [ProductionOrderCreatedEvent]
    
    par Asynchronous Processing
        Kafka->>INV: Consume Event
        INV->>INV: Check raw material availability (Wood, Glue)
        
        Kafka->>AI: Consume Event
        AI->>Gemini: Analyze order size & machine wear
        Gemini-->>AI: "Recommendation: Schedule Saw maintenance after this batch"
        AI->>AI: Log Predictive Insight
    end
    
    Note over MES, Kafka: Later, when workers finish building...
    
    FactoryManager->>UI: Mark Order Complete
    UI->>MES: POST /api/v1/mes/orders/{id}/complete
    MES->>Kafka: Publish [ProductionCompletedEvent]
    
    Kafka->>INV: Consume Event
    INV->>INV: Increment Finished Goods (Chairs) Stock
```

## 2. The Procurement & Inventory Flow

**Scenario**: The factory runs low on raw materials and purchases from a vendor.

```mermaid
sequenceDiagram
    autonumber
    actor PurchasingAgent as Purchasing Agent
    participant PROC as procurement-service
    participant Kafka as Apache Kafka
    participant INV as inventory-service
    participant ACC as accounting-service

    PurchasingAgent->>PROC: Create Purchase Order (1000m Wood)
    PROC->>Kafka: Publish [PurchaseOrderIssuedEvent]
    
    Note over PROC, Kafka: Vendor delivers wood to the loading dock...
    
    PurchasingAgent->>PROC: Mark Goods Received
    PROC->>Kafka: Publish [GoodsReceivedEvent]
    
    par Asynchronous Side Effects
        Kafka->>INV: Consume Event
        INV->>INV: Increase Wood Inventory by 1000m
        
        Kafka->>ACC: Consume Event
        ACC->>ACC: Create Accounts Payable Ledger Entry for Vendor
    end
```

## 3. The B2B Sales Fulfillment Flow

**Scenario**: A retail store bulk-orders furniture through the Dealer Portal.

```mermaid
sequenceDiagram
    autonumber
    actor Dealer as Retail Dealer
    participant DEALER as dealer-portal-service
    participant ERP as erp-central-service
    participant Kafka as Apache Kafka
    participant TMS as tms-service

    Dealer->>DEALER: Submit Wholesale Order
    DEALER->>Kafka: Publish [BulkDiscountAppliedEvent]
    
    Kafka->>ERP: Consume Event
    ERP->>ERP: Convert to Official Sales Order
    ERP->>Kafka: Publish [SalesOrderCreatedEvent]
    
    Kafka->>TMS: Consume Event
    TMS->>TMS: Calculate optimal delivery route for trucks
    TMS->>Kafka: Publish [RouteStartedEvent]
```
