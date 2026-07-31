# AI Analytics Service

A Python-based microservice that acts as the "Brain" of the Furniture ERP.

## Responsibilities
This service subscribes to the shared Apache Kafka event stream and listens for domain events fired by the Java Spring Boot microservices (such as `ProductionOrderCreatedEvent` from `mes-service` or `SalesOrderCreatedEvent` from `erp-central-service`).

When an event arrives, it dynamically constructs a prompt and calls the **Google Gemini API** to generate real-time predictive insights (e.g., predictive maintenance alerts, supply chain reorder suggestions, or customer sentiment analysis) and logs them to the console.

## Setup

1. Create a Python virtual environment:
   ```bash
   python -m venv venv
   # Windows: venv\Scripts\activate
   # Linux/Mac: source venv/bin/activate
   ```

2. Install dependencies:
   ```bash
   pip install -r requirements.txt
   ```

3. Set your Gemini API key:
   ```bash
   export GEMINI_API_KEY="your-api-key-here"
   # or on Windows PowerShell:
   # $env:GEMINI_API_KEY="your-api-key-here"
   ```

4. Run the service:
   ```bash
   uvicorn main:app --reload --port 8095
   ```
