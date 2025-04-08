package dev.lucasquinto.hubspot_api_integration.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import dev.lucasquinto.hubspot_api_integration.model.token.AuthUrlResponse;
import dev.lucasquinto.hubspot_api_integration.service.OAuthService;


@WebMvcTest(OAuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class OAuthControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OAuthService oAuthService;

    @Test
    void genAuthUrl_ReturnsAuthorizationUrl() throws Exception {
        String dummyUrl = "https://dummy.auth.url";
        when(oAuthService.genUrlResponse()).thenReturn(new AuthUrlResponse(dummyUrl));

        mockMvc.perform(get("/oauth/authorize-url"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.authorizationUrl").value(dummyUrl));
    }

    @Test
    void receiveCallBack_ReturnsBadRequest_WhenCodeIsMissing() throws Exception {
        mockMvc.perform(get("/oauth/callback"))
            .andExpect(status().isBadRequest());
    }
}
