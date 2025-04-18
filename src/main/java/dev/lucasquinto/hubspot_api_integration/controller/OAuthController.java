package dev.lucasquinto.hubspot_api_integration.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import dev.lucasquinto.hubspot_api_integration.model.dto.AuthTokenResponseDTO;
import dev.lucasquinto.hubspot_api_integration.model.token.AuthUrlResponse;
import dev.lucasquinto.hubspot_api_integration.service.OAuthService;
import lombok.extern.slf4j.Slf4j;


@RestController
@RequestMapping("oauth")
@Slf4j
public class OAuthController {

    private final OAuthService oAuthService;

    public OAuthController(OAuthService oAuthService) {
        this.oAuthService = oAuthService;
    }

    @GetMapping("authorize-url")
    public ResponseEntity<AuthUrlResponse> genAuthUrl() {
        return ResponseEntity.ok(oAuthService.genUrlResponse() );
    }

    @GetMapping("callback")
    public ResponseEntity<AuthTokenResponseDTO> receiveCallBack(@RequestParam String code) {
        log.debug("Callback received: {}", code);
        AuthTokenResponseDTO authTokenResponseDTO = oAuthService.exchangeCode(code);
        return ResponseEntity.ok(authTokenResponseDTO);
    }
}