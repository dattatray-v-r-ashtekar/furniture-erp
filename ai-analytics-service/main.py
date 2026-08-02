import os
import json
import asyncio
import logging
from fastapi import FastAPI
import google.generativeai as genai
from confluent_kafka import Consumer, KafkaError

logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s [%(levelname)s] %(name)s: %(message)s',
    datefmt='%Y-%m-%d %H:%M:%S'
)
logger = logging.getLogger(__name__)

# Initialize FastAPI
app = FastAPI(title="Furniture ERP AI Analytics Service")

# Configure Gemini API
GEMINI_API_KEY = os.getenv("GEMINI_API_KEY", "YOUR_GEMINI_API_KEY_HERE")
if GEMINI_API_KEY and GEMINI_API_KEY != "YOUR_GEMINI_API_KEY_HERE":
    genai.configure(api_key=GEMINI_API_KEY)

def get_active_model():
    if not GEMINI_API_KEY or GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE":
        return None
    candidate_models = [
        'gemini-3.5-flash-lite',
        'gemini-2.5-flash',
        'gemini-2.0-flash',
        'gemini-1.5-flash',
        'gemini-1.5-flash-latest',
        'gemini-1.5-pro',
        'gemini-pro'
    ]
    try:
        supported = [
            m.name for m in genai.list_models()
            if 'generateContent' in getattr(m, 'supported_generation_methods', [])
        ]
        logger.info(f"Available Gemini models from API: {supported}")
        for cand in candidate_models:
            for s in supported:
                if cand in s or s.endswith(cand):
                    logger.info(f"Selected Gemini model: {s}")
                    return genai.GenerativeModel(s)
        if supported:
            logger.info(f"Selected first available model: {supported[0]}")
            return genai.GenerativeModel(supported[0])
    except Exception as e:
        logger.warning(f"Could not query models dynamically: {e}. Trying fallback list.")
    
    # Direct fallback attempts
    for cand in candidate_models:
        try:
            return genai.GenerativeModel(cand)
        except Exception:
            continue
    return genai.GenerativeModel('gemini-3.5-flash-lite')

model = None

# Kafka Consumer Configuration
KAFKA_BOOTSTRAP_SERVERS = os.getenv("KAFKA_BOOTSTRAP_SERVERS", "kafka:29092")

conf = {
    'bootstrap.servers': KAFKA_BOOTSTRAP_SERVERS,
    'group.id': 'ai-analytics-group',
    'auto.offset.reset': 'earliest',
    'socket.timeout.ms': 10000,
    'reconnect.backoff.ms': 1000,
    'reconnect.backoff.max.ms': 10000
}

topics = [
    'B2CPaymentReceivedEvent',
    'PaymentProcessedEvent',
    'SalesOrderCreatedEvent',
    'ProductionOrderCreatedEvent',
    'ProductionCompletedEvent',
    'MaterialConsumptionRequestedEvent',
    'QualityInspectionFailedEvent'
]

async def analyze_with_gemini(topic: str, payload_str: str):
    global model
    try:
        payload = json.loads(payload_str)
        prompt = f"You are the central AI intelligence for a global furniture manufacturing Enterprise Resource Planning (ERP) system.\n" \
                 f"We just intercepted a domain event from our Kafka cluster.\n" \
                 f"Event Topic: {topic}\n" \
                 f"Event Data: {json.dumps(payload, indent=2)}\n\n" \
                 f"Please provide a concise (3-4 sentences), highly actionable business insight or prediction based on this data. " \
                 f"For example, if it's a production event, suggest predictive maintenance or material staging. If it's a sales event, suggest inventory replenishment."
        
        if not GEMINI_API_KEY or GEMINI_API_KEY == "YOUR_GEMINI_API_KEY_HERE":
            return "MOCK AI INSIGHT: Please configure GEMINI_API_KEY in docker-compose.yml to enable live Gemini AI inference."

        if model is None:
            model = get_active_model()

        if model is None:
            return "AI Insight generated: Processed domain event successfully across ERP workflows."

        try:
            response = model.generate_content(prompt)
            return response.text.strip()
        except Exception as gen_err:
            logger.warning(f"Primary model generation failed: {gen_err}. Refreshing model selection...")
            model = get_active_model()
            if model:
                response = model.generate_content(prompt)
                return response.text.strip()
            raise gen_err
            
    except Exception as e:
        logger.error(f"Failed to analyze with Gemini: {str(e)}")
        return f"Domain event received on {topic}. Automated AI workflow triggered."

def kafka_listener():
    import time
    logger.info(f"Connecting AI Service to Kafka at: {KAFKA_BOOTSTRAP_SERVERS}")
    
    while True:
        try:
            consumer = Consumer(conf)
            consumer.subscribe(topics)
            logger.info(f"AI Service successfully subscribed to topics: {topics}")

            while True:
                msg = consumer.poll(timeout=1.0)
                if msg is None:
                    continue
                if msg.error():
                    # Benign codes when topics have not been published yet or partition EOF
                    if msg.error().code() in (
                        KafkaError._PARTITION_EOF,
                        KafkaError.UNKNOWN_TOPIC_OR_PART,
                        getattr(KafkaError, '_UNKNOWN_TOPIC', -188),
                        getattr(KafkaError, '_UNKNOWN_PARTITION', -190)
                    ):
                        continue
                    else:
                        logger.warning(f"Kafka consumer status: {msg.error()}")
                        time.sleep(2.0)
                        continue
                
                topic = msg.topic()
                value = msg.value().decode('utf-8')
                logger.info(f"Received Event on {topic}: {value}")
                
                try:
                    loop = asyncio.new_event_loop()
                    asyncio.set_event_loop(loop)
                    insight = loop.run_until_complete(analyze_with_gemini(topic, value))
                    logger.info(f"\n{'='*50}\nGEMINI AI INSIGHT:\n{insight}\n{'='*50}\n")
                except Exception as inner_e:
                    logger.error(f"Error executing Gemini insight: {str(inner_e)}")
        except Exception as e:
            logger.error(f"Kafka connection error: {str(e)}. Retrying in 5 seconds...")
            time.sleep(5.0)

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

