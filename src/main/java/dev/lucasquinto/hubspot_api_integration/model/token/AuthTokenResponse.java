package dev.lucasquinto.hubspot_api_integration.model.token;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AuthTokenResponse(
    @JsonProperty("token_type") String tokenType,
    @JsonProperty("refresh_token") String refreshToken,
    @JsonProperty("access_token") String acessToken,
    @JsonProperty("expires_in") String expiresIn
) {}
