package dev.lucasquinto.hubspot_api_integration.service;

import org.springframework.stereotype.Service;

import dev.lucasquinto.hubspot_api_integration.exception.custom.IntegratorClientException;
import dev.lucasquinto.hubspot_api_integration.model.dto.CreateContactRequestDTO;

@Service
public class CrmContactService {

    private IntegrationService integrationService;

    public CrmContactService(IntegrationService integrationService){
        this.integrationService = integrationService;
    }

    public void createContact(CreateContactRequestDTO body) {
        boolean isEmailValid = body.email() != null && body.email().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
        if(!isEmailValid) {
            throw new IntegratorClientException("Email is not valid");
        }

        integrationService.postCrmContactApi(body);
    }
}
