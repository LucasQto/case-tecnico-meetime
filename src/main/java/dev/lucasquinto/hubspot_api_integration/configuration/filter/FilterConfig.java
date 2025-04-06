package dev.lucasquinto.hubspot_api_integration.configuration.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FilterConfig {
    
    @Bean
    public FilterRegistrationBean<TokenValidatorFilter> tokenValidatorFilter() {
        FilterRegistrationBean<TokenValidatorFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new TokenValidatorFilter());
        registrationBean.addUrlPatterns("/hubspot/contacts");
        return registrationBean;
    }
}
