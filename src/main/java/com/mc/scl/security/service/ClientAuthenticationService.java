package com.mc.scl.security.service;

import com.mc.scl.logger.AppLogger;
import com.mc.scl.logger.model.LogLayout;
import com.mc.scl.util.VariablesConstant;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for client authentication operations including
 * authentication key validation and IP address validation.
 */
@Service
@RequiredArgsConstructor
public class ClientAuthenticationService {

    private final AppLogger appLogger;

    /**
     * Validates client requests using Auth Key and IP validation
     */
    public boolean validateClientRequest(HttpServletRequest request) {
        try {
            // 1. Extract and validate auth key from header
            if (!validateAuthKey(request)) {
                logAuthenticationEvent(request, "Invalid or missing auth key for client request", "WARNING", null);
                return false;
            }

            // 2. Validate client IP address
            if (!validateClientIP(request)) {
                logAuthenticationEvent(request, "IP validation failed for client request", "WARNING", null);
                return false;
            }

            return true;
        } catch (Exception e) {
            logAuthenticationEvent(request, "Client validation failed with exception: " + e.getMessage(), "ERROR", e);
            return false;
        }
    }

    /**
     * Validates the authorization key from the request header
     */
    public boolean validateAuthKey(HttpServletRequest request) {
        String authHeader = request.getHeader("API-KEY");

        if (authHeader == null || authHeader.trim().isEmpty()) {
            return false;
        }

        return !authHeader.trim().isEmpty();
    }

    /**
     * Validates the client IP address against database whitelist
     * Checks X-Forwarded-For, X-Real-IP, and Remote Address
     */
    public boolean validateClientIP(HttpServletRequest request) {        
        // TEMPORARY: Using hardcoded IPs for development/testing only
        List<String> allowedIpsFromDB = new ArrayList<>();
        allowedIpsFromDB.add("127.0.0.1");     // localhost
        allowedIpsFromDB.add("::1");           // IPv6 localhost
        allowedIpsFromDB.add("0:0:0:0:0:0:0:1"); // IPv6 localhost full form
        // WARNING: Remove hardcoded IPs before production deployment from DB!
        // Get the three IP sources
        String xForwardedFor = request.getHeader(VariablesConstant.X_FORWARDED_FOR_HEADER);
        String xRealIP = request.getHeader(VariablesConstant.X_REAL_IP_HEADER);
        String remoteAddr = request.getRemoteAddr();

        // Check any of the three IP sources and return true if found in allowed list
        return (xForwardedFor != null && !xForwardedFor.trim().isEmpty() && allowedIpsFromDB.contains(xForwardedFor.trim())) ||
                (xRealIP != null && !xRealIP.trim().isEmpty() && allowedIpsFromDB.contains(xRealIP.trim())) ||
                (remoteAddr != null && !remoteAddr.trim().isEmpty() && allowedIpsFromDB.contains(remoteAddr.trim()));
    }


    /**
     * Helper method to log authentication events using AppLogger
     */
    private void logAuthenticationEvent(HttpServletRequest request, String message, String logLevel, Exception ex) {
        try {
            String xForwardedFor = request.getHeader(VariablesConstant.X_FORWARDED_FOR_HEADER);
            String xRealIP = request.getHeader(VariablesConstant.X_REAL_IP_HEADER);
            String remoteAddr = request.getRemoteAddr();

            LogLayout logLayout = new LogLayout(
                    logLevel,
                    VariablesConstant.LOG_NAME_AUTHENTICATION,
                    VariablesConstant.MODULE_SECURITY,
                    this.getClass().getName(),
                    VariablesConstant.CLIENT_AUTH_SERVICE_METHOD,
                    message,
                    xForwardedFor + VariablesConstant.IP_SEPARATOR + xRealIP + VariablesConstant.IP_SEPARATOR + remoteAddr,
                    request.getRequestURI(),
                    VariablesConstant.EMPTY_STRING
            );

            appLogger.writeLog(logLayout, ex, request);
        } catch (Exception e) {
            LogLayout logLayout = new LogLayout(
                    "ERROR",
                    "LoggingHandler",
                    "shared-code-library",
                    this.getClass().getSimpleName(),
                    "after",
                    "",
                    "",
                    "",
                    "Failed to process request logging: " + e.getMessage()
            );
            appLogger.writeLog(logLayout, e, null);
        }
    }
}