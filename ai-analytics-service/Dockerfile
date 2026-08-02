FROM python:3.10-slim
WORKDIR /app
RUN apt-get update && apt-get install -y --no-install-recommends gcc libc6-dev librdkafka-dev && rm -rf /var/lib/apt/lists/*
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt
COPY . .
EXPOSE 8095
ENTRYPOINT ["uvicorn", "main:app", "--host", "0.0.0.0", "--port", "8095"]
