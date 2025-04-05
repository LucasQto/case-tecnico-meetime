package dev.lucasquinto.hubspot_api_integration.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import dev.lucasquinto.hubspot_api_integration.model.AuthUrlResponse;


@Service
public class OAuthService {
    
    @Value("${spring.security.oauth2.client.provider.hubspot.token-uri}")
    private String hubspotTokenUri;
    @Value("${spring.security.oauth2.client.registration.hubspot.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.hubspot.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.hubspot.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.hubspot.scope}")
    private String scope;
    @Value("${spring.security.oauth2.client.provider.hubspot.authorization-uri}")
    private String providerUrl;

    public AuthUrlResponse genUrlResponse() {
        String authorizationUrl = UriComponentsBuilder
        .fromUriString(providerUrl)
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("scope", scope.replace(",", " "))
        .queryParam("response_type", "code")
        .toUriString();

        return new AuthUrlResponse(authorizationUrl);
    }
}
