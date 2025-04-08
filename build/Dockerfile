FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY target/hubspot-api-integration-1.0.0-SNAPSHOT.jar hubspot-api-integration.jar
COPY build/wait-for-ngrok.sh .

RUN apt-get update && \
    apt-get install -y curl jq && \
    chmod +x wait-for-ngrok.sh