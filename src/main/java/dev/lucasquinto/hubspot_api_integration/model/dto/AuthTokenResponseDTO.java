package dev.lucasquinto.hubspot_api_integration.model.dto;

public record AuthTokenResponseDTO(
    String tokenType,
    String accessToken
) {
    
}
