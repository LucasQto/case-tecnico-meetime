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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import dev.lucasquinto.hubspot_api_integration.configuration.filter.crm.TokenValidatorFilter;
import dev.lucasquinto.hubspot_api_integration.configuration.security.SecurityConfig;
import dev.lucasquinto.hubspot_api_integration.exception.custom.IntegratorClientException;
import dev.lucasquinto.hubspot_api_integration.model.dto.CreateContactRequestDTO;
import dev.lucasquinto.hubspot_api_integration.service.CrmContactService;

@WebMvcTest(controllers = CrmContactController.class)
@Import({SecurityConfig.class, TokenValidatorFilter.class})
class CrmContactControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CrmContactService crmContactService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createContact_ReturnsCreated() throws Exception {
        var request = new CreateContactRequestDTO("example@email.com", "Tester", "Testing");

        doNothing().when(crmContactService).createContact(request);

        mockMvc.perform(post("/crm/contacts")
                .header("Authorization", "Bearer 345346dfgdfhgf464")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(content().string("Contact created successfully"));
    }

    @Test
    void createContact_ReturnsBadRequest_WhenEmailIsInvalid() throws Exception {
        var request = new CreateContactRequestDTO("exampleemail.com", "Tester", "Testing");

        doThrow(new IntegratorClientException("Email is not valid"))
            .when(crmContactService).createContact(any());

        mockMvc.perform(post("/crm/contacts")
                .header("Authorization", "Bearer 345346dfgdfhgf464")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test 
    void createContact_ReturnsUnauthorized() throws Exception {
        var request = new CreateContactRequestDTO("example@email.com", "Tester", "Testing");

        doNothing().when(crmContactService).createContact(request);

        mockMvc.perform(post("/crm/contacts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized());
    }
}