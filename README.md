# Furniture Manufacturing Enterprise Resource Planning (ERP)

A state-of-the-art Enterprise Resource Planning system tailored for a furniture manufacturing business. This system handles B2B (wholesale) and B2C (retail) sales, along with manufacturing, warehouse management, logistics, employees, procurement, and finance.

## Architecture Overview

This project is built using a **Domain-Driven Design (DDD)** and **Event-Driven Microservices** architecture. 
Because manufacturing, warehouse, logistics, and sales are naturally separate business domains that evolve independently, this architecture ensures the system scales easily from a single factory to multiple factories, warehouses, and dealer networks without requiring major redesigns.

### Technology Stack
- **Language**: Java 21 & Python 3
- **Frameworks**: Spring Boot 3.3.0, FastAPI
- **Database**: PostgreSQL (Shared/Dedicated schemas per service)
- **Messaging/Event Bus**: Apache Kafka & Zookeeper
- **Caching**: Redis
- **Security**: Keycloak
- **AI Integration**: Google Gemini API
- **Frontend UI**: Pure HTML5, Vanilla JavaScript, CSS Glassmorphism
- **Build Tool**: Maven (Multi-module Monorepo)

---

## System Modules

The project consists of 14 core Java microservices, 1 Python AI microservice, 1 UI frontend, and a local Monolith Runner.

### 1. Core Microservices (Java / Spring Boot)

| Module | Port | Description |
| :--- | :--- | :--- |
| `inventory-service` | `8081` | Manages raw material and finished goods stock. |
| `procurement-service` | `8082` | Purchases raw materials from external vendors. |
| `erp-central-service` | `8083` | Central management of customer sales orders. |
| `mes-service` | `8084` | Manufacturing Execution: Factory floor production jobs. |
| `wms-service` | `8085` | Warehouse location tracking and physical bins. |
| `tms-service` | `8086` | Transportation Management: Logistics and shipping routes. |
| `crm-service` | `8087` | Customer Relationship Management: Leads and follow-ups. |
| `dealer-portal-service`| `8088` | B2B ordering portal for retail stores and wholesalers. |
| `ecommerce-service` | `8089` | B2C direct-to-consumer online sales. |
| `hrms-service` | `8090` | Human Resources: Employee records and shift tracking. |
| `payroll-service` | `8091` | Salary and tax records. |
| `accounting-service` | `8092` | General ledger financial entries (AP/AR). |
| `qms-service` | `8093` | Quality Management: Defect tracking and inspection. |
| `bi-service` | `8094` | Business Intelligence: KPI reports and dashboards. |

### 2. The AI Brain (Python / FastAPI)
- **`ai-analytics-service` (Port 8095)**: A Python microservice that listens to the Kafka event stream in real-time. When an event fires (e.g., `ProductionOrderCreatedEvent`), it sends the data to the **Google Gemini API** to generate actionable business insights, predictive maintenance alerts, and supply chain routing optimizations.

### 3. The Frontend Dashboard
- **`furniture-erp-ui`**: A Unified Enterprise Command Center built as a Single Page Application (SPA). It uses a premium dark-mode, glassmorphism design and makes dynamic REST API calls to the backend microservices.

### 4. Local Deployment Monolith (Low RAM)
- **`erp-monolith-runner` (Port 8080)**: For cloud deployments, you would deploy the 14 microservices individually. However, for local testing on a laptop, this module combines all 14 domains into a single Tomcat process. It uses only ~1GB of RAM while keeping the original microservice codebase 100% untouched.

---

## Getting Started

### Prerequisites
- JDK 21
- Maven 3.8+
- Docker & Docker Compose
- Google Gemini API Key

### 1. Build the Application Code
Before running Docker, you must compile the Java `.jar` files locally.
```bash
mvn clean install -DskipTests
```

### 2. Set your AI Key
```bash
export GEMINI_API_KEY="your-gemini-key-here"
# Windows PowerShell: $env:GEMINI_API_KEY="your-gemini-key-here"
```

### 3. Run in Docker (Option A: Monolith Mode - Recommended for Laptops)
To run all 14 services inside a single low-memory Docker container alongside your infrastructure and AI Brain:
```bash
docker-compose up --build -d
```
*Note: The monolith runs on port `8081`.*

### 3. Run in Docker (Option B: True Microservices Mode)
If you have a powerful machine (>32GB RAM) and want to simulate a full Kubernetes distributed deployment, you can run all 14 microservices as 14 separate Docker containers simultaneously:
```bash
docker-compose -f docker-compose-microservices.yml up --build -d
```

### 4. Launch the Frontend UI
Navigate to the `furniture-erp-ui` folder and double-click `index.html` to open the Enterprise Command Center in your browser. From there, you can interact with the system and watch the AI Brain analyze your events in real-time!
