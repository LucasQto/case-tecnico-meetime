package dev.lucasquinto.hubspot_api_integration.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import dev.lucasquinto.hubspot_api_integration.model.dto.CreateContactRequestDTO;
import dev.lucasquinto.hubspot_api_integration.service.CrmContactService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("crm")
@Slf4j
public class CrmContactController {
    
    private CrmContactService crmContactServiceService;

    public CrmContactController(CrmContactService crmContactService) {
        this.crmContactServiceService = crmContactService;
    }


    @PostMapping("contacts")
    public ResponseEntity<String> createContact(@RequestBody CreateContactRequestDTO createContactRequest) {
        log.debug("Create contact request received with body: {}", createContactRequest);
        crmContactServiceService.createContact(createContactRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("Contact created successfully");
    }
}
