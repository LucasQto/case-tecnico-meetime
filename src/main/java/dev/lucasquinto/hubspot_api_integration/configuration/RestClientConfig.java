package dev.lucasquinto.hubspot_api_integration.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {
   
    @Bean
    RestClient hubspotCommunicationClient(RestClient.Builder builder) {
        return builder.build();
    }
}
