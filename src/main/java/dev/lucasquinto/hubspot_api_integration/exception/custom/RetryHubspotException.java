package dev.lucasquinto.hubspot_api_integration.exception.custom;

public class RetryHubspotException extends RuntimeException{
    public RetryHubspotException() {
        super("Rate limit reached, retrying...");
    }
}
