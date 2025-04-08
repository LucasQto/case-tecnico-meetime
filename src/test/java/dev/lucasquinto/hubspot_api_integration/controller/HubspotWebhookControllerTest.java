package dev.lucasquinto.hubspot_api_integration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import dev.lucasquinto.hubspot_api_integration.exception.custom.IntegratorClientException;
import dev.lucasquinto.hubspot_api_integration.model.dto.ContactCreationDTO;
import dev.lucasquinto.hubspot_api_integration.service.ContactEventService;
import dev.lucasquinto.hubspot_api_integration.service.CrmContactService;
import dev.lucasquinto.hubspot_api_integration.configuration.security.SecurityConfig;
import dev.lucasquinto.hubspot_api_integration.configuration.filter.webhook.HubspotSignatureFilter;

@WebMvcTest(controllers = HubspotWebhookController.class)
@Import({SecurityConfig.class, HubspotSignatureFilter.class})
class HubspotWebhookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ContactEventService contactEventService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createContact_ReturnsCreated() throws Exception {
        List<ContactCreationDTO> contactCreationDTOList = new ArrayList<>();
        var request = new ContactCreationDTO(
            "123",
            "456",
            "789",
            "123456789",
            "345345645",
            "contact.creation",
            "123456789",
            "123456789",
            "CRM",
            "NEW"
        );

        contactCreationDTOList.add(request);

        doNothing().when(contactEventService).processEvent(contactCreationDTOList);

        mockMvc.perform(post("/hubspot/webhook")
                .header("X-HubSpot-Signature", "123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactCreationDTOList)))
            .andExpect(status().isOk())
            .andExpect(content().string(""));
    }

    @Test
    void createContact_ReturnsBadRequest() throws Exception {
        List<ContactCreationDTO> contactCreationDTOList = new ArrayList<>();
        var request = new ContactCreationDTO(
            "123",
            "456",
            "789",
            "123456789",
            "345345645",
            "contact.creation",
            "123456789",
            "123456789",
            "CRM",
            "NEW"
        );

        contactCreationDTOList.add(request);

        doThrow(new IntegratorClientException("Error processing event")).when(contactEventService).processEvent(any());

        mockMvc.perform(post("http://localhost:8080/hubspot/webhook")
                .header("X-HubSpot-Signature", "123456789")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(contactCreationDTOList)))
            .andExpect(status().isBadRequest());
    }

}
