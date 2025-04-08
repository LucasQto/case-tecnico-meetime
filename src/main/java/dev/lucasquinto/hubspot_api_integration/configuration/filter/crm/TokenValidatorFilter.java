package dev.lucasquinto.hubspot_api_integration.configuration.filter.crm;

import java.io.IOException;
import java.time.Instant;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import dev.lucasquinto.hubspot_api_integration.model.token.RequestTokenContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TokenValidatorFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if(StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {

            RequestTokenContext.setToken(authHeader);

            log.debug("Token used in the current request: {}", authHeader);

            try {
                filterChain.doFilter(request, response);
            } finally {
                RequestTokenContext.clear();
            }
        } else {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");

            String jsonResponse = """
                {
                "timestamp": "%s",
                "status": "Authentication credentials not found",
                "message": "Missing token in request header",
                "code": 401,
                "source": "hubspot-api-integration"
                }
                """.formatted(Instant.now().toString());

            response.getWriter().write(jsonResponse);
            response.getWriter().flush();
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getServletPath();
        if (path == null || path.isEmpty()) {
            path = request.getRequestURI();
        }
        return !path.startsWith("/crm/contacts");
    }
}
