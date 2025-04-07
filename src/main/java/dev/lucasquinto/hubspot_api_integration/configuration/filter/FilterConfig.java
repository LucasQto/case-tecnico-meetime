package dev.lucasquinto.hubspot_api_integration.configuration.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import dev.lucasquinto.hubspot_api_integration.configuration.filter.crm.TokenValidatorFilter;
import dev.lucasquinto.hubspot_api_integration.configuration.filter.webhook.HubspotSignatureFilter;

@Configuration
public class FilterConfig {

    private final HubspotSignatureFilter hubspotSignatureFilter;

    FilterConfig(HubspotSignatureFilter hubspotSignatureFilter) {
        this.hubspotSignatureFilter = hubspotSignatureFilter;
    }
    
    @Bean
    public FilterRegistrationBean<TokenValidatorFilter> tokenValidatorFilter() {
        FilterRegistrationBean<TokenValidatorFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenValidatorFilter());
        registrationBean.addUrlPatterns("/crm/contacts");
        registrationBean.setOrder(0);
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<HubspotSignatureFilter> hubspotSignatureValidatorFilter() {
        FilterRegistrationBean<HubspotSignatureFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(hubspotSignatureFilter);
        registrationBean.addUrlPatterns("/hubspot/webhook");
        registrationBean.setOrder(1);
        return registrationBean;
    }

}
