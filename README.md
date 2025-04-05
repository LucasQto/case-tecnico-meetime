# Integração com HubSpot - Desafio Técnico

## 🎯 Objetivo

Este projeto implementa uma API REST em Java com Spring Boot para integração com a API do HubSpot, utilizando OAuth 2.0 (authorization code flow) e recebimento de webhooks.

Funcionalidades principais:
- Geração de URL para autenticação OAuth2 com o HubSpot
- Recebimento do callback OAuth2 e troca de authorization code por access token
- Criação de contatos no CRM do HubSpot
- Recebimento de notificações via webhook (ex: contact.creation)

---

## ⚙️ Tecnologias e Versões utilizadas
Java: 21
Spring Boot: 3.4.4
Build Tool: Maven