package dev.lucasquinto.hubspot_api_integration.service;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import dev.lucasquinto.hubspot_api_integration.exception.custom.HubspotApiException;
import dev.lucasquinto.hubspot_api_integration.exception.custom.RateLimitExceededException;
import dev.lucasquinto.hubspot_api_integration.exception.custom.RetryHubspotException;
import dev.lucasquinto.hubspot_api_integration.model.crm.contact.HubspotContactPayload;
import dev.lucasquinto.hubspot_api_integration.model.dto.CreateContactRequest;
import dev.lucasquinto.hubspot_api_integration.model.exceptions.HubspotErrorResponse;
import dev.lucasquinto.hubspot_api_integration.model.token.AuthTokenResponse;
import dev.lucasquinto.hubspot_api_integration.model.token.RequestTokenContext;

@Service
@Slf4j
public class IntegrationService {
    
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
    @Value("${hubspot.crm.api-contact.base-url-create}")
    private String crmCreateContactUri;

    @Autowired
    private ObjectMapper objectMapper;
    private final RestClient rsClient;
    private MultiValueMap<String, String> authRequestBody = new LinkedMultiValueMap<>();

    public IntegrationService(RestClient rsClient) {
        this.rsClient = rsClient;
    }

    @PostConstruct
    private void fillAuthRequestBody() {
        authRequestBody.add("grant_type", "");
        authRequestBody.add("client_id", clientId);
        authRequestBody.add("client_secret", clientSecret);
        authRequestBody.add("redirect_uri", redirectUri);
    }

    public String creaAuthUrlResponse() {
        return UriComponentsBuilder
        .fromUriString(providerUrl)
        .queryParam("client_id", clientId)
        .queryParam("redirect_uri", redirectUri)
        .queryParam("scope", scope.replace(",", " "))
        .queryParam("response_type", "code")
        .toUriString();
    }


    public AuthTokenResponse postHubApiToken(Map<String, String> extraParams) {
        extraParams.entrySet().forEach(map -> {
            if(authRequestBody.containsKey(map.getKey())) {
                authRequestBody.set(map.getKey(), map.getValue());
            } else {
                authRequestBody.add(map.getKey(), map.getValue());
            }
        });

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


    public void postCrmContactApi(CreateContactRequest body) {
        HubspotContactPayload payload = new HubspotContactPayload(body);
        String token = RequestTokenContext.getToken();
    
        int attempt = 1;
        int maxRetries = 3;
    
        while (attempt <= maxRetries) {
            int currentAttempt = attempt;
    
            try {
                rsClient.post()
                    .uri(crmCreateContactUri)
                    .contentType(MediaType.APPLICATION_JSON)
                    .header("Authorization", token)
                    .body(payload)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (req, res) -> {
                        handleErrorResponse(res, currentAttempt, maxRetries);
                    })
                    .toBodilessEntity();
    
                return;
    
            } catch (RateLimitExceededException ex) {
                if (attempt >= maxRetries) {
                    log.error("Rate limit exceeded after {} attempts. Giving up.", maxRetries);
                    throw ex;
                }
    
                try {
                    Thread.sleep(ex.getWaitTime());
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    HubspotErrorResponse hubspotErrorResponse = new HubspotErrorResponse("Internal fault", "Thread interrupted during retry wait", 500);
                    throw new HubspotApiException(hubspotErrorResponse, 500);
                }
    
            } catch (HubspotApiException ex) {
                log.error("HubSpot API error: {}", ex.getMessage(), ex);
                throw ex;
            }
    
            attempt++;
        }
    }
    
    private void handleErrorResponse(ClientHttpResponse res, int attempt, int maxRetries) throws IOException {
        int status = res.getStatusCode().value();
        String bodyError = new String(res.getBody().readAllBytes());

        if (status == 429 || bodyError.contains("\"errorType\":\"RATE_LIMIT\"")) {
            String intervalHeader = res.getHeaders().getFirst("X-HubSpot-RateLimit-Interval-Milliseconds");
            int waitTimeMs = intervalHeader != null ? Integer.parseInt(intervalHeader) : 5000;

            log.warn("Rate limit reached. Retrying in {}ms (attempt {}/{})", waitTimeMs, attempt, maxRetries);
            throw new RateLimitExceededException("Rate limit reached. Please wait before retrying", waitTimeMs);
        }

        HubspotErrorResponse hubspotError = objectMapper.readValue(bodyError, HubspotErrorResponse.class);
        throw new HubspotApiException(hubspotError, status);
    }

}