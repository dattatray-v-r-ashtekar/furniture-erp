# AI Integration Strategy

The Furniture ERP utilizes a cutting-edge approach to Artificial Intelligence by entirely decoupling the AI logic from the core Java business services.

## The Strategy: The Passive Observer

Instead of forcing the Java Spring Boot microservices to pause and make synchronous HTTP calls to an AI model (which could slow down the factory systems or cause timeouts), we use the **Passive Observer Pattern**.

1. The Java services go about their normal business at lightning speed, publishing lightweight Domain Events to Apache Kafka.
2. A separate Python microservice, `ai-analytics-service`, acts as a Kafka Consumer group.
3. It passively observes the event stream. When an event of interest flows by, it grabs a copy of the JSON data, formats it into an engineered prompt, and sends it asynchronously to the **Google Gemini API**.
4. The AI returns business insights, which are then logged or can be published back to Kafka on a dedicated `AI_Insights` topic for a dashboard to display.

## Expanding the AI Service

Because the `ai-analytics-service` is written in Python (the industry standard for Data Science and Machine Learning), your data science team can easily expand it beyond API calls to Gemini. 

In the future, the team can install frameworks like **TensorFlow** or **PyTorch** into the `ai-analytics-service` Docker container, allowing them to run custom, locally-trained predictive models directly on the Kafka event stream!
