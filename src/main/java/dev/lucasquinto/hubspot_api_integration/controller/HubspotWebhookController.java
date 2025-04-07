package dev.lucasquinto.hubspot_api_integration.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.lucasquinto.hubspot_api_integration.model.dto.ContactCreationDTO;
import dev.lucasquinto.hubspot_api_integration.service.ContactEventService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping("hubspot")
public class HubspotWebhookController {

    private ContactEventService contactEventService;

    public HubspotWebhookController(ContactEventService contactEventService) {
        this.contactEventService = contactEventService;
    }

    @PostMapping("webhook")
    public ResponseEntity<String> receiveWebhookRequest(@RequestBody List<ContactCreationDTO> contactCreationDTO) {
        contactEventService.processEvent(contactCreationDTO);
        return ResponseEntity.ok("");
    }
}
