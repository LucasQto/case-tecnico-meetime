package dev.lucasquinto.hubspot_api_integration.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import dev.lucasquinto.hubspot_api_integration.dto.AuthTokenResponseDTO;
import dev.lucasquinto.hubspot_api_integration.exception.custom.HubspotApiException;
import dev.lucasquinto.hubspot_api_integration.model.AuthTokenResponse;
import dev.lucasquinto.hubspot_api_integration.model.AuthUrlResponse;
import dev.lucasquinto.hubspot_api_integration.model.exceptions.HubspotErrorResponse;

import jakarta.annotation.PostConstruct;
import com.fasterxml.jackson.databind.ObjectMapper;

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

    private final RestClient rsClient;
    private MultiValueMap<String, String> authRequestBody = new LinkedMultiValueMap<>();
    @Autowired
    private ObjectMapper objectMapper;

    public OAuthService(RestClient rsClient) {
        this.rsClient = rsClient;
    }

    @PostConstruct
    public void fillAuthRequestBody() {
        authRequestBody.add("grant_type", "");
        authRequestBody.add("client_id", clientId);
        authRequestBody.add("client_secret", clientSecret);
        authRequestBody.add("redirect_uri", redirectUri);
    }


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

    public AuthTokenResponseDTO exchangeCode(String code) {
        AuthTokenResponse authTokenResponse;

        authRequestBody.set("grant_type", "authorization_code");
        authRequestBody.add("code", code);

        authTokenResponse = postHubApiToken();
        return new AuthTokenResponseDTO(authTokenResponse.tokenType(), authTokenResponse.acessToken());
    }

    private AuthTokenResponse postHubApiToken() {
        return rsClient.post()
            .uri(hubspotTokenUri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(authRequestBody)
            .retrieve()
            .onStatus(HttpStatusCode::isError, (req, res) -> {
                byte[] errorBytes = res.getBody().readAllBytes();
                String json = new String(errorBytes);

                HubspotErrorResponse hubspotError = objectMapper.readValue(json, HubspotErrorResponse.class);
                throw new HubspotApiException(hubspotError, res.getStatusCode().value());
            })
            .body(AuthTokenResponse.class);
    }
}