package dev.lucasquinto.hubspot_api_integration.exception.custom;

public class IntegratorClientException extends RuntimeException {
    
    String customMessage;

    public IntegratorClientException(String customMessage) {
        super(customMessage);
        this.customMessage = customMessage;
    }

    String getCustomMessage(){
        return customMessage;
    }
}
