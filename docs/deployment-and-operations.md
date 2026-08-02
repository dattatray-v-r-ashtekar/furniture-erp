# Deployment & Operations Guide

This guide describes how to build, run, monitor, and troubleshoot the Furniture ERP system in local, staging, and production environments.

---

## 1. Prerequisites

*   **Java**: OpenJDK 21 (Temurin / Oracle)
*   **Build Tool**: Apache Maven 3.8+
*   **Container Runtime**: Docker Desktop 24+ & Docker Compose v2+
*   **Python**: Python 3.10+ (for AI Analytics & test automation)
*   **AI API Key**: Google Gemini API Key (`GEMINI_API_KEY`)

---

## 2. Environment Configuration (`.env`)

Create or update the `.env` file in the root project directory:

```env
# Google Gemini API Key for AI Brain
GEMINI_API_KEY=your_gemini_api_key_here

# PostgreSQL Database Configuration
POSTGRES_USER=erp_admin
POSTGRES_PASSWORD=erp_password_2026
POSTGRES_DB=furniture_erp_db
POSTGRES_PORT=5432

# Apache Kafka Configuration
KAFKA_PORT=9092
KAFKA_BOOTSTRAP_SERVERS=kafka:29092
ZOOKEEPER_PORT=2181

# Monolith Runner Port
MONOLITH_PORT=8081
```

---

## 3. Build & Packaging

Before running Docker containers, build and package the multi-module Maven project:

```bash
# Option 1: Fast Build (skips test execution for rapid Docker packaging)
mvn clean package -DskipTests

# Option 2: Full Build & Verification (Automatically executes all 15+ Java Unit, Integration, and Embedded E2E tests before installing)
mvn clean install
```

> [!NOTE]
> `mvn clean install` runs the full Maven build lifecycle (`validate` $\rightarrow$ `compile` $\rightarrow$ `test` $\rightarrow$ `package` $\rightarrow$ `verify` $\rightarrow$ `install`), so all Java tests are executed automatically without needing separate invocation.


---

## 4. Deployment Modes

### **Option A: Monolith Container Mode (Recommended for Local Dev & Laptops)**
Runs all 14 domain microservices inside a single Spring Boot container process alongside PostgreSQL, Zookeeper, Kafka, and the Python AI Brain.
*   **Memory Footprint**: ~1.5 GB RAM total
*   **Command**:
    ```bash
    docker-compose up --build -d
    ```
*   **Service Endpoints**:
    *   Unified REST API: `http://localhost:8081`
    *   AI Brain: `http://localhost:8095`
    *   PostgreSQL: `localhost:5432`
    *   Kafka Broker: `localhost:9092`

### **Option B: Distributed Microservices Mode (High-Memory / Kubernetes Simulation)**
Runs each of the 14 microservices in its own isolated Docker container.
*   **Memory Footprint**: ~8 GB - 12 GB RAM
*   **Command**:
    ```bash
    docker-compose -f docker-compose-microservices.yml up --build -d
    ```
*   **Port Allocations**:
    *   `inventory-service`: `8081`
    *   `procurement-service`: `8082`
    *   `erp-central-service`: `8083`
    *   `mes-service`: `8084`
    *   `wms-service`: `8085`
    *   `tms-service`: `8086`
    *   `crm-service`: `8087`
    *   `dealer-portal-service`: `8088`
    *   `ecommerce-service`: `8089`
    *   `hrms-service`: `8090`
    *   `payroll-service`: `8091`
    *   `accounting-service`: `8092`
    *   `qms-service`: `8093`
    *   `bi-service`: `8094`
    *   `ai-analytics-service`: `8095`

---

## 5. Starting the Frontend UI

Open the web command center in any modern browser:
```bash
# Open directly in browser
start furniture-erp-ui/index.html

# Or serve via lightweight HTTP server
npx serve furniture-erp-ui -p 3000
```
Open `http://localhost:3000` (or the direct file path) to access the UI.

---

## 6. Monitoring & Logging

### **Check Container Status**
```bash
docker-compose ps
```

### **Tail Monolith Runner Logs**
```bash
docker logs -f erp-monolith-runner
```

### **Tail AI Brain Logs**
```bash
docker logs -f ai-analytics-service
```

### **Tail Kafka Logs**
```bash
docker logs -f erp-kafka
```

---

## 7. Troubleshooting & Common Issues

| Issue | Root Cause | Solution |
| :--- | :--- | :--- |
| `Kafka Error: Subscribed topic not available` | Kafka topics not initialized before consumer startup. | Spring Boot Kafka auto-creates topics on startup. Wait 10 seconds or send a dummy event to auto-provision topics. |
| `Connection refused: kafka:29092` | Kafka broker is still bootstrapping. | Ensure `depends_on: erp-kafka` has a healthy startup grace period in `docker-compose.yml`. |
| `Jackson No serializer found for ConsumerRecord` | Spring Kafka listener received raw `ConsumerRecord` wrapper without `@Payload` unwrapping. | Handled automatically via updated polymorphic consumer deserializer logic in `ErpCentralKafkaConsumer`, `AccountingKafkaConsumer`, and `MesKafkaConsumer`. |
| `Gemini 404 models/gemini-1.5-flash not found` | Deprecated model alias or incorrect API version in Python SDK. | The system uses `gemini-1.5-flash-latest` or `gemini-2.0-flash` with graceful fallback to rule-based insights if the API key is missing. |
| `Cart items showing wrong price or missing currency` | Currency mismatch between USD (`$`) and INR (`₹`). | All services and UI templates are standardized on Indian Rupee (`₹` / `INR`). |
