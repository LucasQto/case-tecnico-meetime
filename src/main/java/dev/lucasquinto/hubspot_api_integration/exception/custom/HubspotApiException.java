package dev.lucasquinto.hubspot_api_integration.exception.custom;

import dev.lucasquinto.hubspot_api_integration.model.exceptions.HubspotErrorResponse;

public class HubspotApiException extends RuntimeException {
    private final HubspotErrorResponse error;
    private final int httpStatus;

    public HubspotApiException(HubspotErrorResponse error, int httpStatus) {
        super(error.message());
        this.error = error;
        this.httpStatus = httpStatus;
    }

    public HubspotErrorResponse getError() {
        return error;
    }

    public int getHttpStatus() {
        return httpStatus;
    }
}
