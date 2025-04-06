package dev.lucasquinto.hubspot_api_integration.exception.handler;

import java.time.Instant;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.lucasquinto.hubspot_api_integration.exception.custom.HubspotApiException;
import dev.lucasquinto.hubspot_api_integration.exception.custom.IntegratorClientException;
import dev.lucasquinto.hubspot_api_integration.model.exceptions.ErrorResponse;
import dev.lucasquinto.hubspot_api_integration.model.exceptions.HubspotErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Map<Pattern, String> ERROR_PATTERNS = Map.of(
        Pattern.compile("authentication credentials not found", Pattern.CASE_INSENSITIVE), 
        "Token is missing or expired. Please reauthenticate",

        Pattern.compile("rate limit", Pattern.CASE_INSENSITIVE),
        "Rate limit exceeded. Please try again later",

        Pattern.compile("permission", Pattern.CASE_INSENSITIVE),
        "Insufficient permissions to perform this action",

        Pattern.compile("already exists", Pattern.CASE_INSENSITIVE),
        "Contact already exists"
    );

    @ExceptionHandler(HubspotApiException.class)
    public ResponseEntity<ErrorResponse> handleHubspotError(HubspotApiException ex) {
        HubspotErrorResponse hubError = ex.getError();
        String customMessage = mapHubspotError(hubError);

        ErrorResponse response = new ErrorResponse(
            Instant.now().toString(),
            hubError.status(),
            customMessage,
            "Hubspot"
        );

        return ResponseEntity.status(ex.getHttpStatus()).body(response);
    }

    @ExceptionHandler(IntegratorClientException.class)
    public ResponseEntity<ErrorResponse> handleClientError(IntegratorClientException ex) {
        ErrorResponse response = new ErrorResponse(
            Instant.now().toString(),
            "BAD_REQUEST",
            ex.getMessage(),
            "Internal"
        );

        return ResponseEntity.status(400).body(response);
    }

    private String mapHubspotError(HubspotErrorResponse hubError) {
        String message = hubError.message().toLowerCase();
        
        return ERROR_PATTERNS.entrySet().stream()
        .filter(entry -> entry.getKey().matcher(message).find())
        .map(Map.Entry::getValue)
        .findFirst()
        .orElse(message);
    }
}
