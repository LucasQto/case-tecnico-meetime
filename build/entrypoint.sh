#!/bin/bash
set -e

java -jar /app/hubspot-api-integration.jar &

sleep 5

ngrok http 8080 > /dev/null &

sleep 5
NGROK_URL=$(curl -s http://localhost:4040/api/tunnels | grep -Eo 'https://[a-z0-9]+\.ngrok\.io' | head -n 1)

echo "✅ NGROK URL: $NGROK_URL"
echo "➡️ Registre esta URL como webhook/callback no HubSpot"

wait