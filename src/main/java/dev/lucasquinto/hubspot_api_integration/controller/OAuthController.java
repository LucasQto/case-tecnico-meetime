package dev.lucasquinto.hubspot_api_integration.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import dev.lucasquinto.hubspot_api_integration.model.AuthUrlResponse;
import dev.lucasquinto.hubspot_api_integration.service.OAuthService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
@RequestMapping("oauth")
public class OAuthController {

    @Autowired
    private final OAuthService oAuthService = null;

    @GetMapping("authorize-url")
    public ResponseEntity<AuthUrlResponse> genAuthUrl() {
        return ResponseEntity.ok(oAuthService.genUrlResponse() );
    }
}