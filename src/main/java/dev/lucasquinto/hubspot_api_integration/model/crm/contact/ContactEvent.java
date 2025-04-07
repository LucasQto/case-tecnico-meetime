package dev.lucasquinto.hubspot_api_integration.model.crm.contact;

import dev.lucasquinto.hubspot_api_integration.model.dto.ContactCreationDTO;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ContactEvent {
    private Long appId;
    @Id
    private Long eventId;
    private Long subscriptionId;
    private Long portalId;
    private Long occurredAt;
    private String subscriptionType;
    private Long attemptNumber;
    private Long objectId;
    private String changeSource;
    private String changeFlag;

    public static ContactEvent getFromDTO(ContactCreationDTO dto) {
        ContactEvent entity = new ContactEvent();
        entity.setAppId(Long.valueOf(dto.appId()));
        entity.setEventId(Long.valueOf(dto.eventId()));
        entity.setSubscriptionId(Long.valueOf(dto.subscriptionId()));
        entity.setPortalId(Long.valueOf(dto.portalId()));
        entity.setOccurredAt(Long.valueOf(dto.occurredAt()));
        entity.setSubscriptionType(dto.subscriptionType());
        entity.setAttemptNumber(Long.valueOf(dto.attemptNumber()));
        entity.setObjectId(Long.valueOf(dto.objectId()));
        entity.setChangeSource(dto.changeSource());
        entity.setChangeFlag(dto.changeFlag());
        return entity;
    }
}
