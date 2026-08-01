# End-to-End (E2E) Real-Life Test Scenarios

While `manual-test-cases.md` is useful for verifying individual microservices, this document provides true **End-to-End (E2E)** real-life business scenarios. 

In these tests, you will trigger an action at the very beginning of a business process and then trace the "butterfly effect" as Apache Kafka propagates the events across multiple different departments (microservices).

---

## 🛠️ Environment Setup & UI Configuration

Before running the E2E tests, you must start the environment and configure your API keys. You can test everything via the **Frontend UI**!

### Step 1: Set the Gemini AI Key
The Python AI Brain requires a Google Gemini API key to generate insights.
**In Windows PowerShell:**
```powershell
$env:GEMINI_API_KEY="your-actual-api-key-here"
```
**In Linux/Mac Bash:**
```bash
export GEMINI_API_KEY="your-actual-api-key-here"
```

### Step 2: Start the System (Monolith Mode)
To save RAM on your local machine, I have configured the Frontend UI to automatically route all traffic to the Monolith Runner (which runs all 14 services inside a single lightweight process on port `8081`).
```bash
docker-compose up --build -d
```
*(Note: Wait about 45-60 seconds for the Java Monolith, Postgres, and Kafka to fully initialize).*

### Step 3: Launch the React Frontend UI
1. Open a new terminal and start the Vite development server:
   ```bash
   cd furniture-erp-frontend
   npm run dev
   ```
2. Open your browser and navigate to `http://localhost:5173`.
3. You will see the **Enterprise Command Center** with a dark-mode glassmorphism design. 
4. You can use the left sidebar to navigate between departments and execute the scenarios below!

---

## Scenario 1: The B2C Custom Furniture Lifecycle
**Business Story**: An everyday consumer visits the public website and orders a custom-built dining table. Since it's custom, it doesn't exist in the warehouse yet. The factory must build it, log the financials, and hand it to the delivery trucks.

### Step 1: The Customer Checks Out Online
* **Tool**: React UI (Storefront B2C Tab)
* **Action**: Click "Add to Cart" on the Premium Leather Sofa, then click **Checkout Now**.
* **What happens**: The UI sends a request to the `ecommerce-service` which processes the payment and fires `B2CPaymentReceivedEvent`.

### Step 2: Verify Finance & Sales Orchestration
* **Verify Accounting**: Navigate to the **Finance Ledger** tab in the UI. Enter the UUID returned from the checkout to see the revenue entry.
* **Verify Sales**: The `erp-central-service` caught the event and fired `SalesOrderCreatedEvent`. 

### Step 3: Verify the Factory Floor
* **Verify MES**: Navigate to the **Manufacturing** tab in the UI. You can lookup the generated WorkOrder to see its assembly routing.

### Step 4: The Factory Finishes Building It
* **Action**: In the **Manufacturing** tab, you can click "Start Production" and "Complete Production" on the WorkOrder.
* **What happens**: MES fires `ProductionCompletedEvent`.

### Step 5: Verify Warehouse & Logistics
* **Verify Inventory**: Navigate to the **Inventory** tab in the UI. Lookup `SOFA-LEATHER`. The stock should reflect the deduction from the sale.
* **Verify Transport**: Check `tms-service` (`GET http://localhost:8086/api/v1/tms/routes`). You should see an auto-generated delivery route with a FedEx/UPS tracking label attached for the customer's address.

### Step 6: Verify the AI Brain
* **Verify Python AI**: Check the Docker logs for `ai-analytics-service`. You should see that the Google Gemini AI intercepted the factory completion event and logged a predictive insight (e.g., *"Custom Oak Table completed. Recommend scheduling saw blade sharpening due to hard wood usage."*)

---

## Scenario 2: The Raw Material Restock Flow
**Business Story**: The factory runs out of raw wood. Purchasing orders more, it arrives at the loading dock, the warehouse allocates physical shelf space for it, and finance prepares to pay the vendor.

### Step 1: Purchasing Issues a PO
* **Tool**: React UI (Procurement Tab)
* **Action**: Fill in the Vendor ID and SKU, then click "Submit Purchase Order".
* **What happens**: The UI sends the request and `procurement-service` fires `PurchaseOrderIssuedEvent`.

### Step 2: Vendor Delivers the Wood
* **Tool**: React UI (Procurement Tab)
* **Action**: Once the PO is issued, click the "Simulate Truck Arrival & Receive Goods" button.
* **What happens**: `procurement-service` fires `GoodsReceivedEvent`.

### Step 3: Verify Warehouse & Finance
* **Verify WMS**: Navigate to the **Warehouse (WMS)** tab and click Refresh. The `wms-service` caught the event and auto-assigned a physical warehouse bin (e.g., Aisle B, Rack 4) to store the wood.
* **Verify Finance**: Navigate to the **Finance Ledger** tab. The `accounting-service` caught the event and logged an Accounts Payable entry, preparing a check to send to the vendor.

---

## Scenario 3: B2B Wholesale Order with Quality Failure
**Business Story**: A retail giant (like IKEA) orders 100 chairs. The factory builds them, but the Quality Assurance team finds a severe defect and rejects the batch.

### Step 1: Retailer Submits Bulk Order
* **Tool**: React UI (Dealer Portal Tab)
* **Action**: Fill in the bulk order details and click "Place Bulk Order".
* **What happens**: `dealer-portal-service` applies bulk discounts and fires an event, which trickles down to MES to start production.

### Step 2: Factory Completes Production
* **Tool**: React UI (Manufacturing Tab)
* **Action**: Lookup the generated WorkOrder, click "Start Production" then "Complete Production".
* **What happens**: The chairs are built.

### Step 3: QA Inspector Rejects the Batch
* **Tool**: React UI (Quality QMS Tab)
* **Action**: Enter the completed WorkOrder ID and click "Log Critical Defect".
* **What happens**: `qms-service` fires `InspectionFailedEvent`.

### Step 4: Verify Quarantine & Rework
* **Verify Inventory**: The `inventory-service` catches the failure event and instantly moves the 100 chairs into a "Quarantine" state, preventing the logistics team from accidentally loading them onto delivery trucks.
* **Verify MES**: The `mes-service` catches the failure event and automatically schedules a "Rework/Repair" job for the factory workers on the next shift.

---

## Scenario 4: The Employee Lifecycle & Month-End Close
**Business Story**: HR hires a new employee. The employee works a shift. At the end of the month, the CFO generates an executive report aggregating labor costs.

### Step 1: HR Onboards Worker
* **Tool**: React UI (HR & Payroll Tab)
* **Action**: Fill in the employee details and click "Onboard via HRMS".
* **What happens**: `hrms-service` fires `EmployeeOnboardedEvent`. `payroll-service` catches it and creates a tax profile.

### Step 2: Worker Completes a Shift
* **Tool**: React UI (HR & Payroll Tab)
* **Action**: Enter the employee name and hours, then click "Log Shift".
* **What happens**: `hrms-service` fires `ShiftCompletedEvent`. `payroll-service` calculates wages and accrues the liability in the `accounting-service`.

### Step 3: CFO Runs Month-End Close
* **Tool**: React UI (HR & Payroll Tab)
* **Action**: Click the "Execute Global Payroll Run" button.
* **What happens**: Payslips are generated for all employees.

### Step 4: Executive Report
* **Tool**: React UI (BI & AI Insights Tab)
* **Action**: Click "Generate Month-End Report".
* **What happens**: The `bi-service` acts as a massive aggregator. It sweeps through the Kafka history, pulls the revenue generated from Scenario 1, subtracts the material costs from Scenario 2, subtracts Sarah's labor costs from Scenario 4, and renders a stunning executive dashboard representing the true net margin of the factory!
