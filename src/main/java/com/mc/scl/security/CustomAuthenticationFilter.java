package com.mc.scl.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.scl.exception.CommonExceptionMessages;
import com.mc.scl.security.service.ClientAuthenticationService;
import com.mc.scl.security.service.UserAuthenticationService;
import com.mc.scl.util.CommonResponse;
import com.mc.scl.util.VariablesConstant;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends OncePerRequestFilter {

    private final UserAuthenticationService userSessionValidator;
    private final ClientAuthenticationService clientAuthenticationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();

        // Handle client requests with Auth Key + IP validation
        if (requestURI.startsWith(VariablesConstant.CLIENT_URL_PREFIX)) {
            if (!clientAuthenticationService.validateClientRequest(request)) {
                sendErrorResponse(response, CommonExceptionMessages.UNAUTHORIZED_CLIENT);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // Handle postLogin requests with Cognito session validation
        if (requestURI.startsWith(VariablesConstant.POST_LOGIN_URL_PREFIX)) {
            if (!userSessionValidator.validateSession(request)) {
                sendErrorResponse(response, CommonExceptionMessages.UNAUTHORIZED_USER);
                return;
            }
            filterChain.doFilter(request, response);
            return;
        }

        // For all other requests (swagger, random URLs, etc.), continue the filter chain
        // Spring Security configuration will handle the authorization
        filterChain.doFilter(request, response);
    }

    /**
     * Sends a standardized error response in JSON format
     */
    private void sendErrorResponse(HttpServletResponse response, CommonExceptionMessages errorMessage) throws IOException {
        CommonResponse errorResponse = new CommonResponse(errorMessage);
        
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // Create ObjectMapper locally for thread safety
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }

}