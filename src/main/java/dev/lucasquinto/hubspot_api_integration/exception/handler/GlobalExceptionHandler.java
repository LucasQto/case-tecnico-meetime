package dev.lucasquinto.hubspot_api_integration.exception.handler;

import java.time.Instant;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import dev.lucasquinto.hubspot_api_integration.exception.custom.HubspotApiException;
import dev.lucasquinto.hubspot_api_integration.model.exceptions.ErrorResponse;
import dev.lucasquinto.hubspot_api_integration.model.exceptions.HubspotErrorResponse;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(HubspotApiException.class)
    public ResponseEntity<ErrorResponse> handleHubspotError(HubspotApiException ex) {
        HubspotErrorResponse hubError = ex.getError();

        ErrorResponse response = new ErrorResponse(
            Instant.now().toString(),
            hubError.status(),
            hubError.message(),
            ex.getHttpStatus()
        );

        return ResponseEntity.status(400).body(response);
    }

}
