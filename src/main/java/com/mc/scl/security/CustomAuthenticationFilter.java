package com.mc.scl.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mc.scl.exception.CommonException;
import com.mc.scl.exception.CommonExceptionMessages;
import com.mc.scl.security.service.ClientAuthenticationService;
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
    
    private final ClientAuthenticationService clientAuthenticationService;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        if (requestURI.startsWith(VariablesConstant.SERVICE_AUTH_PREFIX)) {
            try {
                if (!clientAuthenticationService.validateServiceRequest(request)) {
                    sendCustomErrorResponse(response, CommonExceptionMessages.UNAUTHORIZED_SERVICE,null,null);
                    return;
                }
                filterChain.doFilter(request, response);
                return;
            } catch (CommonException e) {
                sendCustomErrorResponse(response,null, e.getErrorCode(), e.getErrorMessage());
                return;
            } catch (Exception e) {
                sendCustomErrorResponse(response,CommonExceptionMessages.UNAUTHORIZED_SERVICE,null,null);
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private void sendCustomErrorResponse(HttpServletResponse response, CommonExceptionMessages errorMessage, Integer errorCode, String customMessage) throws IOException {
        CommonResponse errorResponse;
        if (errorCode != null && customMessage != null) {
            errorResponse = new CommonResponse();
            errorResponse.setErrorCode(errorCode);
            errorResponse.setErrorMessage(customMessage);
        } else {
            errorResponse = new CommonResponse(errorMessage);
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonResponse = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(jsonResponse);
        response.getWriter().flush();
    }
}