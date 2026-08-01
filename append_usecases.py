import os

use_cases = """
## 5. Quality Inspection Failure Flow

**Scenario**: A finished sofa fails the Quality Assurance check on the factory floor.

```mermaid
sequenceDiagram
    autonumber
    actor QAInspector as QA Inspector
    participant QMS as qms-service
    participant Kafka as Apache Kafka
    participant MES as mes-service
    participant INV as inventory-service

    QAInspector->>QMS: Log Defect (Torn Fabric)
    QMS->>Kafka: Publish [InspectionFailedEvent]
    
    par Corrective Action
        Kafka->>MES: Consume Event
        MES->>MES: Schedule Rework WorkOrder
        
        Kafka->>INV: Consume Event
        INV->>INV: Quarantine Stock (Prevent Shipping)
    end
```

## 6. Employee Onboarding Flow

**Scenario**: A new factory worker is hired and onboarded.

```mermaid
sequenceDiagram
    autonumber
    actor HRAdmin as HR Admin
    participant HRMS as hrms-service
    participant Kafka as Apache Kafka
    participant PAY as payroll-service
    participant MES as mes-service

    HRAdmin->>HRMS: Create Employee Profile
    HRMS->>Kafka: Publish [EmployeeOnboardedEvent]
    
    par Cross-Department Setup
        Kafka->>PAY: Consume Event
        PAY->>PAY: Create Tax & Salary Profile
        
        Kafka->>MES: Consume Event
        MES->>MES: Grant Factory Floor Terminal Access
    end
```

## 7. Daily Shift & Payroll Processing

**Scenario**: Workers clock out at the end of the day, triggering automated payroll accrual.

```mermaid
sequenceDiagram
    autonumber
    actor Worker
    participant HRMS as hrms-service
    participant Kafka as Apache Kafka
    participant PAY as payroll-service
    participant ACC as accounting-service

    Worker->>HRMS: Clock Out (End of Shift)
    HRMS->>Kafka: Publish [ShiftCompletedEvent]
    
    Kafka->>PAY: Consume Event
    PAY->>PAY: Calculate Hourly Wages + Overtime
    PAY->>Kafka: Publish [PayrollAccruedEvent]
    
    Kafka->>ACC: Consume Event
    ACC->>ACC: Record Liability in General Ledger
```

## 8. Warehouse Bin Reallocation

**Scenario**: A forklift driver moves a pallet of wood from Aisle A to Aisle B to make room.

```mermaid
sequenceDiagram
    autonumber
    actor ForkliftDriver as Forklift Driver
    participant WMS as wms-service
    participant Kafka as Apache Kafka
    participant INV as inventory-service

    ForkliftDriver->>WMS: Scan Pallet & New Bin Location
    WMS->>Kafka: Publish [BinLocationChangedEvent]
    
    Kafka->>INV: Consume Event
    INV->>INV: Update Item Tracking Coordinates
```

## 9. Customer Support Ticket & Refund (B2C)

**Scenario**: An angry online customer returns a damaged table for a refund.

```mermaid
sequenceDiagram
    autonumber
    actor Customer
    participant CRM as crm-service
    participant Kafka as Apache Kafka
    participant ECOM as ecommerce-service
    participant ACC as accounting-service

    Customer->>CRM: Open Ticket (Damaged Item)
    CRM->>CRM: Agent Approves Return
    CRM->>Kafka: Publish [ReturnApprovedEvent]
    
    par Fulfillment Cancellation
        Kafka->>ECOM: Consume Event
        ECOM->>ECOM: Cancel online order status
        
        Kafka->>ACC: Consume Event
        ACC->>ACC: Issue Credit Card Refund
    end
```

## 10. Supplier Delay Notification

**Scenario**: A vendor notifies the factory that lumber delivery will be delayed by a week.

```mermaid
sequenceDiagram
    autonumber
    actor Vendor
    participant PROC as procurement-service
    participant Kafka as Apache Kafka
    participant ERP as erp-central-service
    participant CRM as crm-service

    Vendor->>PROC: Update Delivery ETA (Delayed)
    PROC->>Kafka: Publish [SupplyChainDelayEvent]
    
    par Downstream Alerts
        Kafka->>ERP: Consume Event
        ERP->>ERP: Delay dependent Sales Orders
        
        Kafka->>CRM: Consume Event
        CRM->>CRM: Alert Sales Reps to contact clients
    end
```

## 11. Machine Breakdown (AI Predictive Maintenance)

**Scenario**: A machine on the factory floor begins vibrating abnormally.

```mermaid
sequenceDiagram
    autonumber
    participant MES as mes-service
    participant Kafka as Apache Kafka
    participant AI as ai-analytics-service
    participant Gemini as Google Gemini API
    participant PROC as procurement-service

    MES->>Kafka: Publish [MachineTelemetryEvent]
    
    Kafka->>AI: Consume Event
    AI->>Gemini: Analyze vibration data
    Gemini-->>AI: "High probability of bearing failure in 48 hrs"
    AI->>Kafka: Publish [PredictiveMaintenanceAlertEvent]
    
    Kafka->>PROC: Consume Event
    PROC->>PROC: Auto-order replacement bearings
```

## 12. B2B Contract Negotiation & CRM Lead

**Scenario**: A sales rep closes a large contract with a new hotel chain.

```mermaid
sequenceDiagram
    autonumber
    actor SalesRep as Sales Rep
    participant CRM as crm-service
    participant Kafka as Apache Kafka
    participant DEALER as dealer-portal-service

    SalesRep->>CRM: Mark Deal as "Closed Won"
    CRM->>Kafka: Publish [B2BContractSignedEvent]
    
    Kafka->>DEALER: Consume Event
    DEALER->>DEALER: Create Dealer Login & Discount Tier
```

## 13. Monthly Financial Closing

**Scenario**: The CFO runs the monthly financial close to generate the income statement.

```mermaid
sequenceDiagram
    autonumber
    actor CFO
    participant ACC as accounting-service
    participant Kafka as Apache Kafka
    participant BI as bi-service

    CFO->>ACC: Trigger Month-End Close
    ACC->>ACC: Lock General Ledger
    ACC->>Kafka: Publish [FinancialPeriodClosedEvent]
    
    Kafka->>BI: Consume Event
    BI->>BI: Aggregate Revenue, Labor, & COGS
    BI-->>CFO: Generate Executive Financial Dashboard
```

## 14. Return Materials Authorization (RMA) Logistics

**Scenario**: A delivery truck picks up a returned sofa from a retail dealer.

```mermaid
sequenceDiagram
    autonumber
    actor Driver
    participant TMS as tms-service
    participant Kafka as Apache Kafka
    participant INV as inventory-service
    participant QMS as qms-service

    Driver->>TMS: Mark Return as Picked Up
    TMS->>Kafka: Publish [ReturnTransitStartedEvent]
    
    Note over TMS, Kafka: Truck arrives at warehouse...
    
    Driver->>TMS: Drop off at Receiving Dock
    TMS->>Kafka: Publish [ReturnReceivedEvent]
    
    par Quality & Stock
        Kafka->>QMS: Consume Event
        QMS->>QMS: Schedule Inspection of returned sofa
        
        Kafka->>INV: Consume Event
        INV->>INV: Add to "Returned Goods" quarantine pool
    end
```

## 15. Warehouse Capacity Full Alert

**Scenario**: The warehouse runs out of physical shelf space.

```mermaid
sequenceDiagram
    autonumber
    participant WMS as wms-service
    participant Kafka as Apache Kafka
    participant PROC as procurement-service
    participant MES as mes-service

    WMS->>WMS: Detect 99% Bin Utilization
    WMS->>Kafka: Publish [WarehouseCapacityCriticalEvent]
    
    par Halting Inflow
        Kafka->>PROC: Consume Event
        PROC->>PROC: Pause incoming vendor shipments
        
        Kafka->>MES: Consume Event
        MES->>MES: Slow down production line
    end
```

## 16. Dynamic Pricing Adjustments (AI)

**Scenario**: AI detects high demand and low stock for Leather Sofas, automatically adjusting B2C prices.

```mermaid
sequenceDiagram
    autonumber
    participant INV as inventory-service
    participant Kafka as Apache Kafka
    participant AI as ai-analytics-service
    participant ECOM as ecommerce-service

    INV->>Kafka: Publish [LowStockAlertEvent]
    
    Kafka->>AI: Consume Event
    AI->>AI: Cross-reference with Web Traffic metrics
    AI->>Kafka: Publish [PriceAdjustmentRecommendedEvent]
    
    Kafka->>ECOM: Consume Event
    ECOM->>ECOM: Increase Leather Sofa price by 5%
```

## 17. Transportation Route Delay

**Scenario**: A delivery truck breaks down on the highway.

```mermaid
sequenceDiagram
    autonumber
    actor Driver
    participant TMS as tms-service
    participant Kafka as Apache Kafka
    participant CRM as crm-service

    Driver->>TMS: Report Truck Breakdown
    TMS->>Kafka: Publish [DeliveryRouteDelayedEvent]
    
    Kafka->>CRM: Consume Event
    CRM->>CRM: Auto-email waiting customers with new ETA
```

## 18. Raw Material Quality Rejection

**Scenario**: A vendor delivers glue, but the QA team rejects it for being expired.

```mermaid
sequenceDiagram
    autonumber
    actor QAInspector as QA Inspector
    participant QMS as qms-service
    participant Kafka as Apache Kafka
    participant PROC as procurement-service
    participant ACC as accounting-service

    QAInspector->>QMS: Reject Inbound Shipment
    QMS->>Kafka: Publish [InboundMaterialRejectedEvent]
    
    par Vendor Penalty
        Kafka->>PROC: Consume Event
        PROC->>PROC: Log Vendor Infraction & Demand Replacement
        
        Kafka->>ACC: Consume Event
        ACC->>ACC: Halt Accounts Payable Invoice to Vendor
    end
```

## 19. Employee Promotion & Salary Adjustment

**Scenario**: A factory worker is promoted to Shift Manager.

```mermaid
sequenceDiagram
    autonumber
    actor HRAdmin as HR Admin
    participant HRMS as hrms-service
    participant Kafka as Apache Kafka
    participant PAY as payroll-service

    HRAdmin->>HRMS: Update Job Title (Shift Manager)
    HRMS->>Kafka: Publish [EmployeeRoleChangedEvent]
    
    Kafka->>PAY: Consume Event
    PAY->>PAY: Adjust Hourly Rate to Manager Tier
```

## 20. Year-End BI Executive Summary Generation (AI)

**Scenario**: The CEO requests a massive year-end summary of all factory efficiency metrics.

```mermaid
sequenceDiagram
    autonumber
    actor CEO
    participant BI as bi-service
    participant Kafka as Apache Kafka
    participant AI as ai-analytics-service
    participant Gemini as Google Gemini API

    CEO->>BI: Request Annual Report
    BI->>BI: Gather 12 months of aggregated data
    BI->>Kafka: Publish [AnnualDataAggregatedEvent]
    
    Kafka->>AI: Consume Event
    AI->>Gemini: Prompt: "Write an executive summary on this raw ERP data"
    Gemini-->>AI: "Factory efficiency increased 12%. Bottlenecks exist in Procurement."
    AI-->>CEO: Email Final AI-Generated PDF
```
"""

with open("docs/use-cases.md", "a", encoding="utf-8") as f:
    f.write(use_cases)

print("Successfully appended 16 additional use cases.")
