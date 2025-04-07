
package dev.lucasquinto.hubspot_api_integration.configuration.filter.webhook;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.lucasquinto.hubspot_api_integration.utils.http.CachedBodyHttpServletRequest;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@Component
@Slf4j
public class HubspotSignatureFilter extends OncePerRequestFilter {

    @Value("${spring.security.oauth2.client.registration.hubspot.client-secret}")
    private String appSecret;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

            CachedBodyHttpServletRequest cachedRequest = new CachedBodyHttpServletRequest(request);
            String requestBody = new String(cachedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);

            String signatureHeader = cachedRequest.getHeader("X-HubSpot-Signature");
            log.debug("Signature Header: {}", signatureHeader);
            log.debug("Request Body: {}", requestBody);

            if (signatureHeader == null || signatureHeader.isEmpty()) {
                log.warn("Signature header is missing");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            String expectedSignature = generateSHA256Signature(appSecret + requestBody);
            log.debug("Expected Signature: {}", expectedSignature);

            if (!signatureHeader.equals(expectedSignature)) {
                log.warn("Invalid signature");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }

            // tudo certo, deixa seguir
            filterChain.doFilter(cachedRequest, response);
    }

    private String generateSHA256Signature(String payload) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(payload.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new RuntimeException("Failed to compute signature", e);
        }
    }

    private String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = String.format("%02x", b);
            hexString.append(hex);
        }
        return hexString.toString();
    }
}