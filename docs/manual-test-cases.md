# Comprehensive Manual Test Plan

This document outlines step-by-step instructions to manually test all 14 microservices and the Python AI Analytics Brain. You can use tools like **Postman**, **cURL**, or the **Frontend UI** to execute these.

## Pre-requisites
1. Ensure the system is running locally using Docker:
   ```bash
   docker-compose up --build -d
   ```
2. Wait 30 seconds for Kafka and PostgreSQL to fully initialize.
3. Because the system uses Event-Driven Architecture, executing a test case in one service may automatically trigger data changes in another service!

---

## 1. Inventory Management (`inventory-service`)
**Objective**: Test raw material stock tracking.
* **Action**: `POST http://localhost:8081/api/v1/inventory`
* **Body**: `{"sku": "RAW-WOOD-001", "quantity": 500, "location": "MAIN-WH"}`
* **Expected Result**: HTTP 201 Created. 
* **Verification**: `GET http://localhost:8081/api/v1/inventory/RAW-WOOD-001` should return `quantity: 500`.

## 2. Procurement (`procurement-service`)
**Objective**: Test creating a purchase order for a vendor.
* **Action**: `POST http://localhost:8082/api/v1/procurement/orders`
* **Body**: `{"vendorId": "V-100", "items": [{"sku": "RAW-WOOD-001", "qty": 1000}], "totalCost": 5000.00}`
* **Expected Result**: HTTP 201 Created. Returns a `PurchaseOrderID`.
* **Verification**: This should automatically fire a `PurchaseOrderIssuedEvent` to Kafka.

## 3. Core Sales (`erp-central-service`)
**Objective**: Test creating a central sales order.
* **Action**: `POST http://localhost:8083/api/v1/sales/orders`
* **Body**: `{"customerId": "C-999", "items": [{"sku": "SOFA-LEATHER", "qty": 10}]}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: Check the console logs of `mes-service`. It should have received a `SalesOrderCreatedEvent` from Kafka and automatically queued a manufacturing job.

## 4. Manufacturing Execution (`mes-service`)
**Objective**: Test starting a factory floor job.
* **Action**: `POST http://localhost:8084/api/v1/mes/work-orders`
* **Body**: `{"productId": "SOFA-LEATHER", "quantity": 10, "assemblyLine": "LINE-A"}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: The `ai-analytics-service` should catch the `ProductionOrderCreatedEvent` and prompt Gemini to estimate factory completion time.

## 5. Warehouse Layout (`wms-service`)
**Objective**: Test allocating physical shelf space.
* **Action**: `POST http://localhost:8085/api/v1/wms/bins`
* **Body**: `{"aisle": "A", "rack": "12", "capacity": 1000}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: `GET http://localhost:8085/api/v1/wms/bins` should list the new bin.

## 6. Logistics & Transportation (`tms-service`)
**Objective**: Test delivery routing.
* **Action**: `POST http://localhost:8086/api/v1/tms/routes`
* **Body**: `{"truckId": "TRK-01", "destinationZip": "90210", "cargoWeight": 450.5}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: A `RouteStartedEvent` is published to Kafka.

## 7. Customer Relationship (`crm-service`)
**Objective**: Test logging a B2B sales lead.
* **Action**: `POST http://localhost:8087/api/v1/crm/leads`
* **Body**: `{"companyName": "Hilton Hotels", "contactName": "Alice", "potentialValue": 50000}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: Check database schema `crm_db` for the new record.

## 8. B2B Dealer Portal (`dealer-portal-service`)
**Objective**: Test wholesale purchasing.
* **Action**: `POST http://localhost:8088/api/v1/dealer/orders`
* **Body**: `{"dealerId": "D-101", "bulkDiscountId": "DISC-20", "items": [{"sku": "CHAIR-OAK", "qty": 50}]}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: Ensure the price applied reflects a bulk 20% discount.

## 9. B2C E-Commerce (`ecommerce-service`)
**Objective**: Test consumer cart checkout.
* **Action**: `POST http://localhost:8089/api/v1/ecommerce/checkout`
* **Body**: `{"cartId": "CART-ABC", "paymentMethod": "CREDIT_CARD", "total": 1299.99}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: `accounting-service` should automatically record a revenue entry via Kafka.

## 10. Human Resources (`hrms-service`)
**Objective**: Test employee onboarding.
* **Action**: `POST http://localhost:8090/api/v1/hrms/employees`
* **Body**: `{"firstName": "Bob", "lastName": "Smith", "role": "FACTORY_WORKER", "hourlyRate": 25.50}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: `payroll-service` should automatically create a tax profile for Bob upon hearing the event.

## 11. Payroll (`payroll-service`)
**Objective**: Test running the weekly payroll.
* **Action**: `POST http://localhost:8091/api/v1/payroll/run`
* **Body**: `{"periodStart": "2026-08-01", "periodEnd": "2026-08-07"}`
* **Expected Result**: HTTP 201 Created. Returns a list of generated Payslips.
* **Verification**: `accounting-service` should log a massive liability for employee wages.

## 12. Accounting & Finance (`accounting-service`)
**Objective**: Test manual ledger entry.
* **Action**: `POST http://localhost:8092/api/v1/accounting/ledger`
* **Body**: `{"accountId": "REV-01", "amount": 500.00, "entryType": "CREDIT", "description": "Misc Income"}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: `GET http://localhost:8092/api/v1/accounting/ledger` returns the balanced entry.

## 13. Quality Management (`qms-service`)
**Objective**: Test logging a factory defect.
* **Action**: `POST http://localhost:8093/api/v1/qms/inspections`
* **Body**: `{"workOrderId": "WO-999", "status": "FAILED", "defectReason": "Paint scratched"}`
* **Expected Result**: HTTP 201 Created.
* **Verification**: The `InspectionFailedEvent` is fired. `inventory-service` should quarantine the item.

## 14. Business Intelligence (`bi-service`)
**Objective**: Test aggregate reporting.
* **Action**: `GET http://localhost:8094/api/v1/bi/reports/daily-revenue`
* **Expected Result**: HTTP 200 OK. Returns JSON containing aggregated revenue metrics compiled from historical Kafka events.

## 15. The AI Brain (`ai-analytics-service`)
**Objective**: Verify the AI is dynamically generating insights via Google Gemini.
* **Action**: Perform Test Case #4 (Create a Manufacturing Work Order).
* **Expected Result**: The Python service will intercept the Kafka event. 
* **Verification**: 
  1. Run `docker logs -f ai-analytics-service`.
  2. You will see Python print `"Intercepted ProductionOrderCreatedEvent"`.
  3. Seconds later, you will see the actual predictive response returned from the **Google Gemini API** printed in the terminal logs!
