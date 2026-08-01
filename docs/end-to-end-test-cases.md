# End-to-End (E2E) Real-Life Test Scenarios

While `manual-test-cases.md` is useful for verifying individual microservices, this document provides true **End-to-End (E2E)** real-life business scenarios. 

In these tests, you will trigger an action at the very beginning of a business process and then trace the "butterfly effect" as Apache Kafka propagates the events across multiple different departments (microservices).

---

## Scenario 1: The B2C Custom Furniture Lifecycle
**Business Story**: An everyday consumer visits the public website and orders a custom-built dining table. Since it's custom, it doesn't exist in the warehouse yet. The factory must build it, log the financials, and hand it to the delivery trucks.

### Step 1: The Customer Checks Out Online
* **Tool**: Postman / cURL
* **Action**: `POST http://localhost:8089/api/v1/ecommerce/checkout`
* **Body**: `{"customerId": "CUST-123", "item": "CUSTOM-OAK-TABLE", "totalPaid": 1500.00}`
* **What happens**: The `ecommerce-service` processes the payment and fires `B2CPaymentReceivedEvent`.

### Step 2: Verify Finance & Sales Orchestration
* **Verify Accounting**: Check `accounting-service` (`GET http://localhost:8092/api/v1/accounting/ledger`). You should see a new $1500 revenue entry.
* **Verify Sales**: The `erp-central-service` caught the event and fired `SalesOrderCreatedEvent`. 

### Step 3: Verify the Factory Floor
* **Verify MES**: Check the `mes-service` (`GET http://localhost:8084/api/v1/mes/work-orders`). You should see a brand new auto-generated WorkOrder to build the `CUSTOM-OAK-TABLE`.

### Step 4: The Factory Finishes Building It
* **Action**: Simulate the factory workers completing the job by sending a request to the MES service:
  `POST http://localhost:8084/api/v1/mes/work-orders/{WorkOrderID_From_Step3}/complete`
* **What happens**: MES fires `ProductionCompletedEvent`.

### Step 5: Verify Warehouse & Logistics
* **Verify Inventory**: Check `inventory-service` (`GET http://localhost:8081/api/v1/inventory/CUSTOM-OAK-TABLE`). The stock should now be `1` (ready to ship).
* **Verify Transport**: Check `tms-service` (`GET http://localhost:8086/api/v1/tms/routes`). You should see an auto-generated delivery route with a FedEx/UPS tracking label attached for the customer's address.

### Step 6: Verify the AI Brain
* **Verify Python AI**: Check the Docker logs for `ai-analytics-service`. You should see that the Google Gemini AI intercepted the factory completion event and logged a predictive insight (e.g., *"Custom Oak Table completed. Recommend scheduling saw blade sharpening due to hard wood usage."*)

---

## Scenario 2: The Raw Material Restock Flow
**Business Story**: The factory runs out of raw wood. Purchasing orders more, it arrives at the loading dock, the warehouse allocates physical shelf space for it, and finance prepares to pay the vendor.

### Step 1: Purchasing Issues a PO
* **Action**: `POST http://localhost:8082/api/v1/procurement/orders`
* **Body**: `{"vendorId": "LUMBER-INC", "sku": "RAW-WOOD-001", "qty": 5000}`
* **What happens**: `procurement-service` fires `PurchaseOrderIssuedEvent`.

### Step 2: Vendor Delivers the Wood
* **Action**: The truck arrives. The loading dock worker marks it received.
  `POST http://localhost:8082/api/v1/procurement/orders/{OrderID}/receive`
* **What happens**: `procurement-service` fires `GoodsReceivedEvent`.

### Step 3: Verify Warehouse & Finance
* **Verify WMS**: The `wms-service` should catch the event and auto-assign a physical warehouse bin (e.g., Aisle B, Rack 4) to store the 5000 units of wood.
* **Verify Finance**: The `accounting-service` should catch the event and log an **Accounts Payable** entry, preparing a check to send to LUMBER-INC.

---

## Scenario 3: B2B Wholesale Order with Quality Failure
**Business Story**: A retail giant (like IKEA) orders 100 chairs. The factory builds them, but the Quality Assurance team finds a severe defect and rejects the batch.

### Step 1: Retailer Submits Bulk Order
* **Action**: `POST http://localhost:8088/api/v1/dealer/orders`
* **Body**: `{"dealerId": "IKEA-NY", "item": "STANDARD-CHAIR", "qty": 100}`
* **What happens**: `dealer-portal-service` applies bulk discounts and fires an event, which trickles down to MES to start production.

### Step 2: Factory Completes Production
* **Action**: `POST http://localhost:8084/api/v1/mes/work-orders/{OrderID}/complete`
* **What happens**: 100 chairs are built.

### Step 3: QA Inspector Rejects the Batch
* **Action**: The QA team finds the chair legs are wobbly.
  `POST http://localhost:8093/api/v1/qms/inspections`
* **Body**: `{"workOrderId": "{OrderID}", "status": "FAILED", "reason": "Wobbly legs"}`
* **What happens**: `qms-service` fires `InspectionFailedEvent`.

### Step 4: Verify Quarantine & Rework
* **Verify Inventory**: The `inventory-service` catches the failure event and instantly moves the 100 chairs into a "Quarantine" state, preventing the logistics team from accidentally loading them onto delivery trucks.
* **Verify MES**: The `mes-service` catches the failure event and automatically schedules a "Rework/Repair" job for the factory workers on the next shift.

---

## Scenario 4: The Employee Lifecycle & Month-End Close
**Business Story**: HR hires a new employee. The employee works a shift. At the end of the month, the CFO generates an executive report aggregating labor costs.

### Step 1: HR Onboards Worker
* **Action**: `POST http://localhost:8090/api/v1/hrms/employees`
* **Body**: `{"name": "Sarah Connor", "role": "MACHINE_OPERATOR", "hourlyRate": 30.00}`
* **What happens**: `hrms-service` fires `EmployeeOnboardedEvent`. `payroll-service` catches it and creates a tax profile.

### Step 2: Worker Completes a Shift
* **Action**: `POST http://localhost:8090/api/v1/hrms/shifts/complete`
* **Body**: `{"employeeName": "Sarah Connor", "hoursWorked": 8}`
* **What happens**: `hrms-service` fires `ShiftCompletedEvent`. `payroll-service` calculates $240 in wages and accrues the liability in the `accounting-service`.

### Step 3: CFO Runs Month-End Close
* **Action**: `POST http://localhost:8094/api/v1/bi/reports/monthly-summary`
* **What happens**: The `bi-service` acts as a massive aggregator. It sweeps through the Kafka history, pulls the revenue generated from Scenario 1, subtracts the material costs from Scenario 2, subtracts Sarah's labor costs from Scenario 4, and returns a single JSON dashboard representing the true net profit of the factory.
