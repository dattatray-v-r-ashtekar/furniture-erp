import os

service_dir = "ai-analytics-service"
os.makedirs(service_dir, exist_ok=True)

requirements_content = """fastapi==0.103.2
uvicorn==0.23.2
confluent-kafka==2.3.0
google-generativeai==0.5.2
pydantic==2.4.2
python-dotenv==1.0.0
"""

main_content = """import os
import json
import asyncio
import logging
from fastapi import FastAPI
import google.generativeai as genai
from confluent_kafka import Consumer, KafkaError

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# Initialize FastAPI
app = FastAPI(title="Furniture ERP AI Analytics Service")

# Configure Gemini API
# NOTE: Ensure GEMINI_API_KEY environment variable is set
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY_HERE")
genai.configure(api_key=GEMINI_API_KEY)
model = genai.GenerativeModel('gemini-1.5-flash')

# Kafka Consumer Configuration
conf = {
    'bootstrap.servers': 'localhost:9092',
    'group.id': 'ai-analytics-group',
    'auto.offset.reset': 'earliest'
}

topics = [
    'ProductionOrderCreatedEvent', 
    'MaterialConsumptionRequestedEvent',
    'SalesOrderCreatedEvent',
    'QualityInspectionFailedEvent'
]

async def analyze_with_gemini(topic: str, payload_str: str):
    try:
        payload = json.loads(payload_str)
        prompt = f"You are the central AI intelligence for a global furniture manufacturing Enterprise Resource Planning (ERP) system.\\n" \\
                 f"We just intercepted a domain event from our Kafka cluster.\\n" \\
                 f"Event Topic: {topic}\\n" \\
                 f"Event Data: {json.dumps(payload, indent=2)}\\n\\n" \\
                 f"Please provide a very brief (3-4 sentences), highly actionable business insight or prediction based on this data. " \\
                 f"For example, if it's a production event, suggest predictive maintenance. If it's a sales event, suggest inventory restocking."
        
        # NOTE: This requires a valid API key to succeed.
        if GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE":
            logger.warning("Mocking Gemini response because API key is not configured.")
            return "MOCK AI INSIGHT: Please configure the GEMINI_API_KEY in ai-analytics-service to see real insights."

        response = model.generate_content(prompt)
        return response.text
    except Exception as e:
        logger.error(f"Failed to analyze with Gemini: {str(e)}")
        return "Error analyzing data."

def kafka_listener():
    consumer = Consumer(conf)
    consumer.subscribe(topics)
    logger.info(f"AI Service subscribed to topics: {topics}")

    try:
        while True:
            msg = consumer.poll(timeout=1.0)
            if msg is None:
                continue
            if msg.error():
                if msg.error().code() == KafkaError._PARTITION_EOF:
                    continue
                else:
                    logger.error(msg.error())
                    break
            
            topic = msg.topic()
            value = msg.value().decode('utf-8')
            logger.info(f"Received Event on {topic}: {value}")
            
            # Use asyncio to run the analysis without blocking consumer heavily (in a real app, use a queue)
            loop = asyncio.new_event_loop()
            asyncio.set_event_loop(loop)
            insight = loop.run_until_complete(analyze_with_gemini(topic, value))
            
            logger.info(f"\\n{'='*50}\\nGEMINI AI INSIGHT:\\n{insight}\\n{'='*50}\\n")
            
    finally:
        consumer.close()

@app.on_event("startup")
async def startup_event():
    logger.info("Starting AI Kafka Consumer in background...")
    # Run the Kafka consumer in a background thread
    import threading
    thread = threading.Thread(target=kafka_listener, daemon=True)
    thread.start()

@app.get("/")
def read_root():
    return {"status": "AI Analytics Service is running and listening to Kafka."}

"""

readme_content = """# AI Analytics Service

A Python-based microservice that acts as the "Brain" of the Furniture ERP.

## Responsibilities
This service subscribes to the shared Apache Kafka event stream and listens for domain events fired by the Java Spring Boot microservices (such as `ProductionOrderCreatedEvent` from `mes-service` or `SalesOrderCreatedEvent` from `erp-central-service`).

When an event arrives, it dynamically constructs a prompt and calls the **Google Gemini API** to generate real-time predictive insights (e.g., predictive maintenance alerts, supply chain reorder suggestions, or customer sentiment analysis) and logs them to the console.

## Setup

1. Create a Python virtual environment:
   ```bash
   python -m venv venv
   # Windows: venv\\Scripts\\activate
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
"""

with open(f"{service_dir}/requirements.txt", "w", encoding="utf-8") as f:
    f.write(requirements_content)
    
with open(f"{service_dir}/main.py", "w", encoding="utf-8") as f:
    f.write(main_content)

with open(f"{service_dir}/README.md", "w", encoding="utf-8") as f:
    f.write(readme_content)

print("Generated ai-analytics-service/")
