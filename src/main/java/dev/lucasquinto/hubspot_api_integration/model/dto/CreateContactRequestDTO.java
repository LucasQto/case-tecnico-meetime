package dev.lucasquinto.hubspot_api_integration.model.dto;

public record CreateContactRequestDTO(
    String email,
    String firstname,
    String lastname
) {}
