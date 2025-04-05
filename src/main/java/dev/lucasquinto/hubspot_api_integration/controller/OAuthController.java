package dev.lucasquinto.hubspot_api_integration.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import dev.lucasquinto.hubspot_api_integration.model.AuthUrlResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("oauth")
public class OAuthController {

    @Value("${spring.security.oauth2.client.registration.hubspot.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.hubspot.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.registration.hubspot.scope}")
    private String scope;
    @Value("${spring.security.oauth2.client.provider.hubspot.authorization-uri}")
    private String providerUrl;

    @GetMapping("authorize-url")
    public ResponseEntity<AuthUrlResponse> genAuthUrl() {
        String authorizationUrl = UriComponentsBuilder
        .fromUriString(providerUrl)
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("scope", scope.replace(",", " "))
        .queryParam("response_type", "code")
        .toUriString();

        return ResponseEntity.ok(new AuthUrlResponse(authorizationUrl));
    }
}