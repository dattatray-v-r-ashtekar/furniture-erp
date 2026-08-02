# Furniture ERP - REST API Reference Manual

This document provides a comprehensive REST API specification for all microservices in the Furniture ERP system.

---

## Global Standards

*   **Base URL (Monolith / Docker Mode)**: `http://localhost:8081`
*   **Base URL (Distributed Microservices Mode)**: Direct ports `8081` through `8095`
*   **Data Format**: `application/json;charset=UTF-8`
*   **Currency**: Indian Rupee (`₹` / `INR`)
*   **Standard Identifiers**: `UUID v4` (e.g., `3fa85f64-5717-4562-b3fc-2c963f66afa6`)

---

## 1. E-Commerce Service (`/api/v1/ecommerce`)

Handles Direct-to-Consumer (B2C) store operations, shopping carts, and order checkout.

### `POST /api/v1/ecommerce/orders`
Creates and checks out an e-commerce order with multiple line items, publishing `B2CPaymentReceivedEvent` to Kafka.

#### Request Body
```json
{
  "referenceCode": "ORD-B2C-20260802-001",
  "totalAmount": 87500.50,
  "items": [
    {
      "sku": "TABLE-DINING",
      "name": "Solid Oak Dining Table",
      "quantity": 1,
      "price": 62500.50
    },
    {
      "sku": "CHAIR-OFFICE",
      "name": "Ergonomic Mesh Chair",
      "quantity": 2,
      "price": 12500.00
    }
  ]
}
```

#### Response (`201 Created`)
```json
{
  "id": "e7b0a880-9a3b-488b-a1bc-8cf0b4a7d6e1",
  "referenceCode": "ORD-B2C-20260802-001",
  "totalAmount": 87500.50,
  "status": "PAID",
  "items": [
    {
      "id": "52f865f1-bd12-4c28-944a-a71e2c8a24c5",
      "sku": "TABLE-DINING",
      "name": "Solid Oak Dining Table",
      "quantity": 1,
      "price": 62500.50
    },
    {
      "id": "84a7e930-cf2a-4318-8ee5-8eb648fcbc74",
      "sku": "CHAIR-OFFICE",
      "name": "Ergonomic Mesh Chair",
      "quantity": 2,
      "price": 12500.00
    }
  ]
}
```

### `GET /api/v1/ecommerce/orders`
Retrieves all placed online orders.

#### Response (`200 OK`)
```json
[
  {
    "id": "e7b0a880-9a3b-488b-a1bc-8cf0b4a7d6e1",
    "referenceCode": "ORD-B2C-20260802-001",
    "totalAmount": 87500.50,
    "status": "PAID",
    "items": [ ... ]
  }
]
```

---

## 2. ERP Central Service (`/api/v1/erp`)

Core sales order management engine that consolidates B2B/B2C transactions into enterprise sales orders.

### `POST /api/v1/erp/sales-orders`
Manually create a consolidated Sales Order.

#### Request Body
```json
{
  "orderId": "e7b0a880-9a3b-488b-a1bc-8cf0b4a7d6e1",
  "referenceCode": "ORD-B2C-20260802-001",
  "totalAmount": 87500.50,
  "items": [
    {
      "sku": "TABLE-DINING",
      "name": "Solid Oak Dining Table",
      "quantity": 1,
      "price": 62500.50
    },
    {
      "sku": "CHAIR-OFFICE",
      "name": "Ergonomic Mesh Chair",
      "quantity": 2,
      "price": 12500.00
    }
  ]
}
```

#### Response (`200 OK`)
```json
{
  "id": "e7b0a880-9a3b-488b-a1bc-8cf0b4a7d6e1",
  "referenceCode": "ORD-B2C-20260802-001",
  "totalAmount": 87500.50,
  "status": "CONFIRMED",
  "items": [
    {
      "id": "f8a92350-410a-40a2-9b29-1c9f4d1e2a56",
      "sku": "TABLE-DINING",
      "name": "Solid Oak Dining Table",
      "quantity": 1,
      "price": 62500.50
    },
    {
      "id": "9bc03144-884d-45f8-a120-cf37a6b12984",
      "sku": "CHAIR-OFFICE",
      "name": "Ergonomic Mesh Chair",
      "quantity": 2,
      "price": 12500.00
    }
  ]
}
```

### `GET /api/v1/erp/sales-orders`
Lists all synchronized enterprise Sales Orders.

---

## 3. Accounting Service (`/api/v1/accounting`)

Manages the General Ledger, double-entry bookkeeping, and real-time revenue/cost tracking.

### `POST /api/v1/accounting/ledgers`
Records a new General Ledger debit/credit balance.

#### Request Body
```json
{
  "orderId": "e7b0a880-9a3b-488b-a1bc-8cf0b4a7d6e1",
  "referenceCode": "ORD-B2C-20260802-001",
  "accountName": "REVENUE-B2C",
  "entryType": "CREDIT",
  "amount": 87500.50,
  "description": "B2C Order Revenue ORD-B2C-20260802-001 (Solid Oak Dining Table x1, Ergonomic Mesh Chair x2)"
}
```

#### Response (`200 OK`)
```json
{
  "id": "42f9b8c0-21a4-469b-819a-3d2319efb501",
  "orderId": "e7b0a880-9a3b-488b-a1bc-8cf0b4a7d6e1",
  "referenceCode": "ORD-B2C-20260802-001",
  "accountName": "REVENUE-B2C",
  "entryType": "CREDIT",
  "amount": 87500.50,
  "description": "B2C Order Revenue ORD-B2C-20260802-001 (Solid Oak Dining Table x1, Ergonomic Mesh Chair x2)"
}
```

### `GET /api/v1/accounting/ledgers`
Retrieves all general ledger double-entry transactions.

---

## 4. MES Service (`/api/v1/mes`)

Manufacturing Execution System for managing factory floor production orders and work progression.

### `POST /api/v1/mes/production-orders`
Plans a new factory production run.

#### Request Body
```json
{
  "productSku": "TABLE-DINING",
  "targetQuantity": 1
}
```

#### Response (`200 OK`)
```json
{
  "id": "a98c0d12-4fb5-488a-9214-5d9c2409f871",
  "productSku": "TABLE-DINING",
  "targetQuantity": 1,
  "status": "PLANNED"
}
```

### `POST /api/v1/mes/production-orders/{id}/complete`
Marks a manufacturing run as finished, triggering `ProductionCompletedEvent` to Kafka.

#### Response (`200 OK`)
```json
{
  "id": "a98c0d12-4fb5-488a-9214-5d9c2409f871",
  "productSku": "TABLE-DINING",
  "targetQuantity": 1,
  "status": "COMPLETED"
}
```

### `GET /api/v1/mes/production-orders`
Fetches all planned, active, and completed factory production orders.

---

## 5. Inventory Service (`/api/v1/inventory`)

Tracks raw materials, assemblies, and finished furniture stock across plant facilities.

### `POST /api/v1/inventory/stocks`
Creates or updates SKU stock records.

#### Request Body
```json
{
  "sku": "TABLE-DINING",
  "quantity": 15
}
```

#### Response (`200 OK`)
```json
{
  "id": "18cfb042-5f81-4bc9-93e1-382a9d80112c",
  "sku": "TABLE-DINING",
  "quantity": 15
}
```

### `GET /api/v1/inventory/stocks`
Lists all real-time inventory counts.

---

## 6. Procurement Service (`/api/v1/procurement`)

Manages purchase orders with raw material suppliers (timber, leather, hardware, varnishes).

### `POST /api/v1/procurement/purchase-orders`
Creates a Purchase Order for external vendors.

#### Request Body
```json
{
  "supplierName": "Teakwood Importers India Ltd",
  "rawMaterialSku": "RAW-TEAK-LOGS",
  "quantity": 250,
  "totalPrice": 450000.00
}
```

### `GET /api/v1/procurement/purchase-orders`
Retrieves all supplier purchase orders and fulfillment status.

---

## 7. Warehouse Management Service (`/api/v1/wms`)

Manages physical bin layouts, pallet locations, and warehouse capacity.

### `POST /api/v1/wms/bins`
Registers a new warehouse bin location.

#### Request Body
```json
{
  "binCode": "AISLE-3-SHELF-B",
  "sku": "TABLE-DINING",
  "capacity": 20
}
```

### `GET /api/v1/wms/bins`
Lists all warehouse storage bins and their current utilization.

---

## 8. Transportation Management Service (`/api/v1/tms`)

Handles fleet dispatch, delivery routes, carrier scheduling, and tracking.

### `POST /api/v1/tms/routes`
Schedules a delivery dispatch route.

#### Request Body
```json
{
  "origin": "Bangalore Central Plant",
  "destination": "Hyderabad Distribution Hub",
  "vehicleNumber": "KA-01-EA-9821",
  "driverName": "Rajesh Kumar"
}
```

### `GET /api/v1/tms/routes`
Lists all active and completed freight delivery routes.

---

## 9. CRM Service (`/api/v1/crm`)

Customer Relationship Management for client accounts, leads, and tier rankings.

### `POST /api/v1/crm/customers`
Registers a customer profile.

#### Request Body
```json
{
  "name": "Acme Living Spaces Ltd",
  "email": "procurement@acmeliving.in",
  "tier": "GOLD",
  "loyaltyScore": 1500
}
```

### `GET /api/v1/crm/customers`
Fetches all registered customer profiles and CRM scores.

---

## 10. Dealer Portal Service (`/api/v1/dealer-portal`)

B2B wholesale portal for dealer distribution networks.

### `POST /api/v1/dealer-portal/orders`
Places a wholesale order for retail dealers.

#### Request Body
```json
{
  "dealerId": "DEALER-SOUTH-402",
  "sku": "BED-KING",
  "quantity": 20,
  "totalPrice": 750000.00
}
```

### `GET /api/v1/dealer-portal/orders`
Lists all wholesale B2B distributor orders.

---

## 11. HRMS Service (`/api/v1/hrms`)

Human Resources Management for personnel, departments, and factory shift schedules.

### `POST /api/v1/hrms/employees`
Registers a factory employee.

#### Request Body
```json
{
  "name": "Anil Sharma",
  "department": "Carpentry & Joinery",
  "shift": "MORNING_SHIFT",
  "designation": "Master Craftsman"
}
```

### `GET /api/v1/hrms/employees`
Lists all factory and administrative employee records.

---

## 12. Payroll Service (`/api/v1/payroll`)

Payroll disbursement, tax withholding, and compensation records.

### `POST /api/v1/payroll/slips`
Generates an employee salary slip.

#### Request Body
```json
{
  "employeeId": "EMP-3042",
  "monthYear": "AUGUST_2026",
  "baseSalary": 65000.00,
  "deductions": 4500.00
}
```

### `GET /api/v1/payroll/slips`
Lists all generated salary slips.

---

## 13. Quality Management Service (`/api/v1/qms`)

Quality assurance inspection, defect analysis, and batch approval.

### `POST /api/v1/qms/inspections`
Performs and logs a quality inspection report.

#### Request Body
```json
{
  "productSku": "TABLE-DINING",
  "passed": true,
  "inspectorName": "Vikram Patel",
  "notes": "Surface smoothness test: 100% compliant. No finish blemishes."
}
```

### `GET /api/v1/qms/inspections`
Lists all factory quality assurance records.

---

## 14. Business Intelligence Service (`/api/v1/bi`)

Aggregates enterprise KPIs across departments for executive dashboards.

### `POST /api/v1/bi/reports`
Publishes an executive KPI snapshot.

#### Request Body
```json
{
  "reportTitle": "Q3 Executive Manufacturing & Revenue Report",
  "totalRevenue": 14250000.00,
  "activeWorkOrders": 48
}
```

### `GET /api/v1/bi/reports`
Lists all generated executive BI report summaries.

---

## 15. AI Analytics Service (`http://localhost:8095/analyze`)

Python FastAPI service providing AI-driven real-time root-cause analysis and operational forecasting via the Google Gemini LLM.

### `POST /analyze`
Analyzes domain events and production bottlenecks.

#### Request Body
```json
{
  "eventType": "ProductionOrderCreatedEvent",
  "data": {
    "productSku": "BED-KING",
    "targetQuantity": 50,
    "timestamp": 1785665000
  }
}
```

#### Response (`200 OK`)
```json
{
  "insight": "High volume production request detected for SKU BED-KING. Recommended Action: Pre-allocate 150 board feet of teak wood from Aisle 3 and schedule double assembly shifts.",
  "status": "success",
  "confidenceScore": 0.94
}
```
