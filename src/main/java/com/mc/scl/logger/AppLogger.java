package com.mc.scl.logger;

import com.mc.scl.logger.model.LogLayout;
import com.mc.scl.util.VariablesConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Level;
import org.json.JSONObject;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AppLogger {

    private static final Logger logger = LogManager.getLogger(AppLogger.class);
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final Environment env;

    // Cache expensive operations for better performance
    private volatile String cachedMinimumLogLevel;
    private volatile boolean cachedWriteLogEnabled;
    private volatile String cachedHostName;
    private volatile String cachedIpAddress;
    private volatile String cachedPort;

    public AppLogger(Environment env) {
        this.env = env;
    }

    @PostConstruct
    private void initializeCache() {
        // Cache configuration values that rarely change
        this.cachedMinimumLogLevel = env.getProperty("minimumLogLevel", VariablesConstant.INFO);
        this.cachedWriteLogEnabled = Boolean.parseBoolean(env.getProperty("writeLog", "true"));
        this.cachedPort = env.getProperty("server.port", "8080");

        // Cache expensive network operations
        try {
            InetAddress localHost = InetAddress.getLocalHost();
            this.cachedHostName = localHost.getHostName();
            this.cachedIpAddress = localHost.getHostAddress();
        } catch (UnknownHostException e) {
            logger.warn("Unable to determine host information: {}", e.getMessage());
            this.cachedHostName = "unknown";
            this.cachedIpAddress = "unknown";
        }
    }

    public void writeLog(LogLayout logLayout, Exception ex) {
        writeLog(logLayout, ex, null);
    }

    public void writeLog(LogLayout logLayout, Exception ex, HttpServletRequest request) {
        // Early validation and exception logging
        if (Objects.nonNull(ex)) {
            logger.error(ex.getMessage(), ex);
        }

        // Validate log level with null safety
        String validatedLogLevel = getSafeString(logLayout.getLogLevel());
        if (!isValidLogLevel(validatedLogLevel)) {
            validatedLogLevel = VariablesConstant.INFO;
        }

        // Early exit if logging is disabled or log level doesn't meet minimum threshold
        if (!cachedWriteLogEnabled || !isLogLevelAllowed(validatedLogLevel)) {
            return;
        }

        try {
            // Create log entry using cached values and thread-safe operations
            JSONObject logEntry = createLogEntry(logLayout, ex, request);

            // Write the log using Log4j2 directly
            logMessage(logEntry.toString(), validatedLogLevel);

        } catch (Exception e) {
            // Fallback logging - never let logging failure break the application
            logger.error("Failed to write custom log entry: {}", e.getMessage(), e);
        }
    }

    private boolean isValidLogLevel(String logLevel) {
        return VariablesConstant.INFO.equals(logLevel) ||
                VariablesConstant.WARNING.equals(logLevel) ||
                VariablesConstant.ERROR.equals(logLevel);
    }

    private String getSafeString(String value) {
        return value != null ? value : "";
    }

    private boolean isLogLevelAllowed(String logLevel) {
        return switch (cachedMinimumLogLevel) {
            case VariablesConstant.INFO -> true; // Allow all levels
            case VariablesConstant.WARNING -> VariablesConstant.WARNING.equals(logLevel) || VariablesConstant.ERROR.equals(logLevel);
            case VariablesConstant.ERROR -> VariablesConstant.ERROR.equals(logLevel);
            default -> true; // Default to allowing all if config is unknown
        };
    }

    private JSONObject createLogEntry(LogLayout logLayout, Exception ex, HttpServletRequest request) {
        JSONObject item = new JSONObject();

        // Use thread-safe LocalDateTime instead of SimpleDateFormat
        String currentTime = LocalDateTime.now().format(DATE_TIME_FORMATTER);

        // Basic log information with null safety
        item.put("logTime", currentTime);
        item.put("level", getSafeString(logLayout.getLogLevel()));
        item.put("logName", getSafeString(logLayout.getLogName()));
        item.put("moduleName", getSafeString(logLayout.getModule()));
        item.put("className", getSafeString(logLayout.getClassName()));
        item.put("methodName", getSafeString(logLayout.getMethodName()));
        item.put("message", getSafeString(logLayout.getMessage()));

        // Data fields with null safety
        item.put("data1", getSafeString(logLayout.getData1()));
        item.put("data2", getSafeString(logLayout.getData2()));
        item.put(VariablesConstant.ADDITIONAL_DATA_KEY, getSafeString(logLayout.getAdditionalData()));

        // System information (using cached values)
        item.put("hostName", cachedHostName);
        item.put("ipAddress", cachedIpAddress);
        item.put("port", cachedPort);

        // Exception information (from parameter)
        if (ex != null) {
            String exceptionMessage = ex.getMessage() != null ? ex.getMessage() : ex.getClass().getName();
            item.put(VariablesConstant.EXCEPTION_SMALL, exceptionMessage);
        } else {
            item.put(VariablesConstant.EXCEPTION_SMALL, "");
        }

        // Request information using the passed request (your approach is better)
        addRequestInformation(request, item);

        return item;
    }


    private void addRequestInformation(HttpServletRequest request, JSONObject item) {
        try {
            if (request != null) {
                processValidRequest(request, item);
            } else {
                setDefaultRequestInfo(item);
            }
        } catch (Exception e) {
            logger.debug("Error processing request information: {}", e.getMessage());
            setDefaultRequestInfo(item);
        }
    }

    private void processValidRequest(HttpServletRequest request, JSONObject item) {
        // Set request URL (path variables included, query params logged separately in requestParams)
        item.put(VariablesConstant.URL_KEY, getSafeString(request.getRequestURI()));

        // Process request parameters based on HTTP method
        String requestMethod = request.getMethod();
        Map<String, Object> requestParams = extractRequestParameters(request, requestMethod);

        // Set request parameters from actual HTTP request (query params, form data, JSON body)
        item.put(VariablesConstant.REQUEST_PARAMS_KEY, requestParams.isEmpty() ? "{}" : requestParams.toString());
    }

    private Map<String, Object> extractRequestParameters(HttpServletRequest request, String requestMethod) {
        Map<String, Object> requestParams = new ConcurrentHashMap<>();

        // For all methods, first try to get query parameters
        if (request.getParameterMap() != null) {
            request.getParameterMap().forEach((key, value) -> {
                if (value != null && value.length > 0) {
                    requestParams.put(key, value[0]);
                }
            });
        }

        // For POST, PUT, PATCH - also try to extract body parameters
        if ("POST".equals(requestMethod) || "PUT".equals(requestMethod) || "PATCH".equals(requestMethod)) {
            requestParams.put(VariablesConstant.CONTENT_TYPE_KEY, request.getContentType());
            requestParams.put(VariablesConstant.REQUEST_BODY_KEY, request.getAttribute("request"));
        }

        return requestParams;
    }

    private void setDefaultRequestInfo(JSONObject item) {
        item.put(VariablesConstant.URL_KEY, "");
        item.put(VariablesConstant.REQUEST_PARAMS_KEY, "{}");
    }

    /**
     * Internal method to log messages with appropriate log levels
     * (Replaces the functionality of the external Log8 class)
     */
    private void logMessage(String message, String logLevel) {
        Level level = Level.INFO;
        if (VariablesConstant.WARNING.equals(logLevel)) {
            level = Level.WARN;
        } else if (VariablesConstant.ERROR.equals(logLevel)) {
            level = Level.ERROR;
        }
        logger.log(level, message);
    }

}