package dev.lucasquinto.hubspot_api_integration.model.dto;

public record CreateContactRequest(
    String email,
    String firstname,
    String lastname
) {}
