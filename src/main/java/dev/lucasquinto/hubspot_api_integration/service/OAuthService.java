package dev.lucasquinto.hubspot_api_integration.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import dev.lucasquinto.hubspot_api_integration.model.dto.AuthTokenResponseDTO;
import dev.lucasquinto.hubspot_api_integration.model.token.AuthTokenResponse;
import dev.lucasquinto.hubspot_api_integration.model.token.AuthUrlResponse;

@Service
public class OAuthService {

    private IntegrationService integrationService;

    public OAuthService(IntegrationService integrationService) {
        this.integrationService = integrationService;
    }

    public AuthUrlResponse genUrlResponse() {
        return new AuthUrlResponse(integrationService.creaAuthUrlResponse());
    }

    public AuthTokenResponseDTO exchangeCode(String code) {
        AuthTokenResponse authTokenResponse;
        Map<String, String> extraParams = new LinkedHashMap<>();

        extraParams.put("grant_type", "authorization_code");
        extraParams.put("code", code);

        authTokenResponse = integrationService.postHubApiToken(extraParams);
        return new AuthTokenResponseDTO(authTokenResponse.tokenType(), authTokenResponse.acessToken());
    }
}