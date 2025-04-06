package dev.lucasquinto.hubspot_api_integration.model.exceptions;

public record HubspotErrorResponse(
    String status,
    String message,
    int httpCode
) {}
