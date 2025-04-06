package dev.lucasquinto.hubspot_api_integration.model.exceptions;

public record ErrorResponse(
    String timestamp,
    String error,
    String message,
    int httpCode
) {}
