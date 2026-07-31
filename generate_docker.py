import os
import shutil

# List of all java modules and their ports
java_modules = {
    "inventory-service": 8081,
    "procurement-service": 8082,
    "erp-central-service": 8083,
    "mes-service": 8084,
    "wms-service": 8085,
    "tms-service": 8086,
    "crm-service": 8087,
    "dealer-portal-service": 8088,
    "ecommerce-service": 8089,
    "hrms-service": 8090,
    "payroll-service": 8091,
    "accounting-service": 8092,
    "qms-service": 8093,
    "bi-service": 8094,
    "erp-monolith-runner": 8080
}

# 1. Rename existing docker-compose.yml
if os.path.exists("docker-compose.yml") and not os.path.exists("docker-compose-infra.yml"):
    shutil.move("docker-compose.yml", "docker-compose-infra.yml")
    print("Renamed docker-compose.yml to docker-compose-infra.yml")

# 2. Create Java Dockerfiles
java_dockerfile_template = """FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/*.jar app.jar
EXPOSE {port}
ENTRYPOINT ["java", "-jar", "app.jar"]
"""

for module, port in java_modules.items():
    if os.path.exists(module):
        with open(f"{module}/Dockerfile", "w") as f:
            f.write(java_dockerfile_template.format(port=port))
        print(f"Created {module}/Dockerfile")

# 3. Create AI Python Dockerfile
ai_dockerfile = """FROM python:3.10-slim
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8095
ENTRYPOINT ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8095"]
"""
if os.path.exists("ai-analytics-service"):
    with open("ai-analytics-service/Dockerfile", "w") as f:
        f.write(ai_dockerfile)
    print("Created ai-analytics-service/Dockerfile")

# 4. Generate docker-compose.yml (Monolith + Infra)
infra_yaml = """version: '3.8'

services:
  postgres:
    image: postgres:15-alpine
    container_name: erp-postgres
    environment:
      POSTGRES_USER: erp_user
      POSTGRES_PASSWORD: erp_password
      POSTGRES_DB: erp_db
    ports:
      - "5432:5432"
    networks:
      - erp-network

  redis:
    image: redis:7-alpine
    container_name: erp-redis
    ports:
      - "6379:6379"
    networks:
      - erp-network

  zookeeper:
    image: confluentinc/cp-zookeeper:7.4.0
    container_name: erp-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - "2181:2181"
    networks:
      - erp-network

  kafka:
    image: confluentinc/cp-kafka:7.4.0
    container_name: erp-kafka
    depends_on:
      - zookeeper
    ports:
      - "9092:9092"
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: 'zookeeper:2181'
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:29092,PLAINTEXT_HOST://localhost:9092
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
      KAFKA_GROUP_INITIAL_REBALANCE_DELAY_MS: 0
    networks:
      - erp-network

  keycloak:
    image: quay.io/keycloak/keycloak:24.0.4
    container_name: erp-keycloak
    environment:
      KEYCLOAK_ADMIN: admin
      KEYCLOAK_ADMIN_PASSWORD: admin
    command: start-dev
    ports:
      - "8080:8080"
    networks:
      - erp-network
"""

monolith_compose = infra_yaml + """
  erp-monolith-runner:
    build:
      context: ./erp-monolith-runner
    container_name: erp-monolith-runner
    depends_on:
      - postgres
      - kafka
    ports:
      - "8081:8080" # Exposed on 8081 since keycloak uses 8080
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/erp_db
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092
    networks:
      - erp-network

  ai-analytics-service:
    build:
      context: ./ai-analytics-service
    container_name: ai-analytics-service
    depends_on:
      - kafka
    ports:
      - "8095:8095"
    environment:
      - GEMINI_API_KEY=${GEMINI_API_KEY}
    networks:
      - erp-network

networks:
  erp-network:
    driver: bridge
"""

with open("docker-compose.yml", "w") as f:
    f.write(monolith_compose)
print("Created docker-compose.yml (Monolith Mode)")

# 5. Generate docker-compose-microservices.yml
micro_compose = infra_yaml

for module, port in java_modules.items():
    if module == "erp-monolith-runner":
        continue
    micro_compose += f"""
  {module}:
    build:
      context: ./{module}
    container_name: {module}
    depends_on:
      - postgres
      - kafka
    ports:
      - "{port}:{port}"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/erp_db
      - SPRING_KAFKA_BOOTSTRAP_SERVERS=kafka:29092
    networks:
      - erp-network
"""

micro_compose += """
  ai-analytics-service:
    build:
      context: ./ai-analytics-service
    container_name: ai-analytics-service
    depends_on:
      - kafka
    ports:
      - "8095:8095"
    environment:
      - GEMINI_API_KEY=${GEMINI_API_KEY:-}
    networks:
      - erp-network

networks:
  erp-network:
    driver: bridge
"""

with open("docker-compose-microservices.yml", "w") as f:
    f.write(micro_compose)
print("Created docker-compose-microservices.yml")
