import os

modules = {
    "common-domain": {
        "title": "Common Domain Library",
        "description": "Shared Domain-Driven Design (DDD) base classes.",
        "port": "N/A (Library)",
        "details": "Provides foundational DDD patterns such as `AggregateRoot`, `BaseEntity`, and the `DomainEvent` marker interface. All microservices depend on this module to ensure a consistent domain model architecture across the enterprise."
    },
    "common-messaging": {
        "title": "Common Messaging Library",
        "description": "Shared messaging interfaces and Kafka publishers.",
        "port": "N/A (Library)",
        "details": "Defines the `DomainEventPublisher` interface. Implementing microservices use this library to emit events asynchronously over Apache Kafka, facilitating the Event-Driven Microservices architecture."
    },
    "inventory-service": {
        "title": "Inventory Microservice",
        "description": "Manages raw material and finished goods stock levels.",
        "port": "8081",
        "details": "Core Entities: `StockItem`.\n\nResponsible for holding stock quantities of raw materials (like wood, glue, fabric) and finished furniture goods. It listens to external events (like GoodsReceived or ProductionCompleted) to adjust stock levels accordingly."
    },
    "procurement-service": {
        "title": "Procurement Microservice",
        "description": "Handles purchasing raw materials from external vendors.",
        "port": "8082",
        "details": "Core Entities: `PurchaseOrder`, `PurchaseOrderLine`.\nDomain Events: `PurchaseOrderIssuedEvent`, `GoodsReceivedEvent`.\n\nResponsible for creating purchase orders. Once a vendor delivers the goods, this service emits a `GoodsReceivedEvent`, alerting the `inventory-service` to increase available stock."
    },
    "erp-central-service": {
        "title": "ERP Central Microservice",
        "description": "Centralized management of customer sales orders.",
        "port": "8083",
        "details": "Core Entities: `SalesOrder`, `SalesOrderLine`.\nDomain Events: `SalesOrderCreatedEvent`.\n\nThe central hub for incoming customer orders (from eCommerce or B2B portals). It orchestrates order fulfillment by communicating with the warehouse and logistics systems."
    },
    "mes-service": {
        "title": "Manufacturing Execution System (MES)",
        "description": "Controls factory floor production jobs.",
        "port": "8084",
        "details": "Core Entities: `ProductionOrder`, `WorkOrder`.\nDomain Events: `MaterialConsumptionRequestedEvent`, `ProductionCompletedEvent`.\n\nTracks the conversion of raw materials into finished furniture. Each `ProductionOrder` contains multiple `WorkOrder`s representing specific machine operations (Cutting, Assembly, Polishing)."
    },
    "wms-service": {
        "title": "Warehouse Management System (WMS)",
        "description": "Manages physical storage locations and bin capacities.",
        "port": "8085",
        "details": "Core Entities: `WarehouseBin`, `BinMovement`.\nDomain Events: `BinCapacityReachedEvent`.\n\nHandles the physical put-away and picking of inventory. While `inventory-service` tracks total quantities, WMS tracks the exact aisle, rack, and shelf where items are physically stored."
    },
    "tms-service": {
        "title": "Transportation Management System (TMS)",
        "description": "Plans multi-stop logistics and shipping routes.",
        "port": "8086",
        "details": "Core Entities: `DeliveryRoute`, `DeliveryStop`.\nDomain Events: `RouteStartedEvent`.\n\nCoordinates fleet vehicles to deliver finished furniture to retail dealers or directly to consumers."
    },
    "crm-service": {
        "title": "Customer Relationship Management (CRM)",
        "description": "Manages leads, follow-ups, and interaction history.",
        "port": "8087",
        "details": "Core Entities: `CustomerProfile`, `InteractionLog`.\nDomain Events: `CustomerConvertedEvent`.\n\nUsed by the sales team to track potential leads and their journey toward becoming paying customers."
    },
    "dealer-portal-service": {
        "title": "Dealer Portal (B2B)",
        "description": "B2B wholesale ordering portal for retail stores.",
        "port": "8088",
        "details": "Core Entities: `WholesaleOrder`, `WholesaleItem`.\nDomain Events: `BulkDiscountAppliedEvent`.\n\nAllows third-party retail stores to bulk-order furniture. Integrates directly with `erp-central-service`."
    },
    "ecommerce-service": {
        "title": "E-Commerce System (B2C)",
        "description": "B2C direct-to-consumer online sales storefront.",
        "port": "8089",
        "details": "Core Entities: `OnlineOrder`, `CartItem`.\nDomain Events: `PaymentProcessedEvent`.\n\nHandles the consumer-facing shopping cart, checkout, and payment processing before submitting orders to `erp-central-service`."
    },
    "hrms-service": {
        "title": "Human Resources Management System (HRMS)",
        "description": "Factory and office employee tracking and shift scheduling.",
        "port": "8090",
        "details": "Core Entities: `EmployeeRecord`, `AttendanceLog`.\nDomain Events: `ShiftAssignedEvent`.\n\nTracks employee data, factory shift allocations, attendance, and performance."
    },
    "payroll-service": {
        "title": "Payroll Microservice",
        "description": "Calculates salaries and tax deductions.",
        "port": "8091",
        "details": "Core Entities: `SalarySlip`, `TaxDeduction`.\nDomain Events: `SalaryDisbursedEvent`.\n\nIntegrates with `hrms-service` and `accounting-service` to process employee compensation."
    },
    "accounting-service": {
        "title": "Accounting Microservice",
        "description": "General ledger and financial tracking.",
        "port": "8092",
        "details": "Core Entities: `GeneralLedger`, `JournalEntry`.\nDomain Events: `LedgerBalancedEvent`.\n\nManages Accounts Payable (from `procurement-service`), Accounts Receivable (from `erp-central-service`), and general financial health."
    },
    "qms-service": {
        "title": "Quality Management System (QMS)",
        "description": "Tracks defects during and after production.",
        "port": "8093",
        "details": "Core Entities: `QualityInspection`, `DefectLog`.\nDomain Events: `InspectionFailedEvent`.\n\nWorks closely with `mes-service` to log defective furniture builds and perform root-cause analysis on the factory floor."
    },
    "bi-service": {
        "title": "Business Intelligence (BI) Service",
        "description": "Generates KPI reports and data dashboards.",
        "port": "8094",
        "details": "Core Entities: `DashboardReport`, `KpiMetric`.\nDomain Events: `ReportGeneratedEvent`.\n\nAggregates data across the enterprise (MES, ERP, CRM) to provide management with insights into profitability and production efficiency."
    }
}

readme_template = """# {title}

{description}

## Module Details
- **Port**: `{port}`
- **Context**: Furniture Manufacturing ERP Architecture

## Architecture & Responsibilities

{details}

## Running Locally

To run this module locally (assuming the Docker infrastructure is running):

```bash
# From the root directory of the monorepo
mvn spring-boot:run -pl {module_name}
```

## Integration

This module is part of the larger Event-Driven Microservices ecosystem. It relies on:
- `common-domain`: For Domain-Driven Design base classes.
- `common-messaging`: For Apache Kafka event publishing.
- A shared PostgreSQL database (`erp_db`) or its own isolated schema.
- Apache Kafka (`localhost:9092`) for cross-service asynchronous communication.
"""

for module_name, info in modules.items():
    if os.path.exists(module_name):
        readme_path = f"{module_name}/README.md"
        content = readme_template.format(
            title=info["title"],
            description=info["description"],
            port=info["port"],
            details=info["details"],
            module_name=module_name
        )
        with open(readme_path, "w") as f:
            f.write(content)
        print(f"Created {readme_path}")
    else:
        print(f"Skipping {module_name}, directory not found.")
