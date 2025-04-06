package dev.lucasquinto.hubspot_api_integration.model.crm.contact;

import java.util.HashMap;
import java.util.Map;

import dev.lucasquinto.hubspot_api_integration.model.dto.CreateContactRequest;

public record HubspotContactPayload(Map<String, Object> properties) {
    public HubspotContactPayload(CreateContactRequest body) {
        this(buildProperties(body));
    }

    private static Map<String, Object> buildProperties(CreateContactRequest body) {
        Map<String, Object> props = new HashMap<>();
        props.put("email", body.email());
        props.put("firstname", body.firstname());
        props.put("lastname", body.lastname());
        return props;
    }
}
