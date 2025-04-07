package dev.lucasquinto.hubspot_api_integration.configuration.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions().sameOrigin())
            .authorizeHttpRequests(authz -> authz
                .requestMatchers(
                    "/oauth/callback",
                    "/oauth/authorize-url",
                    "/crm/contacts",
                    "/hubspot/webhook",
                    "/h2-console/**"
                ).permitAll()
                .anyRequest().denyAll()
            )
            .httpBasic(httpBasic -> httpBasic.disable())
            .oauth2Login(oauth2 -> oauth2.disable());

        return http.build();
    }
}