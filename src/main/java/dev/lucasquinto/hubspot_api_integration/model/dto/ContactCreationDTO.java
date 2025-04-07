package dev.lucasquinto.hubspot_api_integration.model.dto;

public record ContactCreationDTO( 
        String appId,
        String eventId,
        String subscriptionId,
        String portalId,
        String occurredAt,
        String subscriptionType,
        String attemptNumber,
        String objectId,
        String changeSource,
        String changeFlag
) {}