package dev.lucasquinto.hubspot_api_integration.exception.custom;

import lombok.Getter;

@Getter
public class RateLimitExceededException extends RuntimeException {

    String message;
    Integer waitTime;

    public RateLimitExceededException(String customMessage, Integer waitTime) {
        super(customMessage);
        this.message = customMessage;
        this.waitTime = waitTime; 
    }

}
