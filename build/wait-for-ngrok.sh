#!/bin/sh

echo "🔁 Aguardando o Ngrok gerar uma URL pública..."

until curl -s http://ngrok:4040/api/tunnels | grep -q "public_url"; do
  sleep 2
done

NGROK_URL=$(curl -s http://ngrok:4040/api/tunnels | jq -r '.tunnels[0].public_url')

echo "✅ NGROK URL: $NGROK_URL"

export HUBSPOT_REDIRECT_URI="${NGROK_URL}/oauth/callback"

echo "HUBSPOT_REDIRECT_URI=${HUBSPOT_REDIRECT_URI}" > /app/ngrok.env