package dev.lucasquinto.hubspot_api_integration.service;

import java.util.List;

import org.springframework.stereotype.Service;

import dev.lucasquinto.hubspot_api_integration.model.crm.contact.ContactEvent;
import dev.lucasquinto.hubspot_api_integration.model.dto.ContactCreationDTO;
import dev.lucasquinto.hubspot_api_integration.repository.ContactEventRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ContactEventService {
    
    private ContactEventRepository contactEventRepository;

    public ContactEventService(ContactEventRepository contactEventRepository) {
        this.contactEventRepository = contactEventRepository;
    }

    public void processEvent(List<ContactCreationDTO> contactCreationDTO) {
        log.debug("A batch of events receveid, size: {}", String.valueOf(contactCreationDTO.size()));

        contactCreationDTO.forEach(dto -> {
            log.debug("New event received: {}", dto);
            contactEventRepository.save(ContactEvent.getFromDTO(dto));
        });
    }
}
