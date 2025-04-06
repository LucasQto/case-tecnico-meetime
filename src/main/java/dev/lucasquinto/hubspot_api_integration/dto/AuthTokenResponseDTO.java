package dev.lucasquinto.hubspot_api_integration.dto;

public record AuthTokenResponseDTO(
    String tokenType,
    String accessToken
) {
    
}
