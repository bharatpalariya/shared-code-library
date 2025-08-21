package com.mc.scl.util;

public class VariablesConstant {

    private VariablesConstant() {
        // Private constructor to prevent instantiation
    }

    // Logging related constants
    public static final String INFO = "INFO";
    public static final String WARNING = "WARNING";
    public static final String ERROR = "ERROR";
    public static final String SUCCESS = "SUCCESS";
    public static final String RUNTIME = "runtime";
    public static final String EXCEPTION = "Exception";
    public static final String REQUEST = "request";
    public static final String LOG_NAME_AUTHENTICATION = "Authentication";
    public static final String MODULE_SECURITY = "security";
    public static final String IP_SEPARATOR = "-";
    public static final String EMPTY_STRING = "";
    public static final String REQUEST_PARAMS_KEY = "requestParams";
    public static final String ADDITIONAL_DATA_KEY = "additionalData";
    public static final String URL_KEY = "url";
    public static final String CONTENT_TYPE_KEY = "contentType";
    public static final String REQUEST_BODY_KEY = "requestBody";
    public static final String EXCEPTION_SMALL = "exception";
    public static final String LOGGING_HANDLER = "LoggingHandler";
    public static final String LOGGER = "logger";
    public static final String MODULE_COMMON = "common";
    public static final String COMMON_UTILITY = "commonUtility";

    // Authentication related constants
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String X_FORWARDED_FOR_HEADER = "X-Forwarded-For";
    public static final String X_REAL_IP_HEADER = "X-Real-IP";
    public static final String SERVICE_CODE = "serviceCode";
    public static final String SERVICE_AUTH_KEY = "serviceAuthKey";

    // URL path constants
    public static final String SERVICE_AUTH_PREFIX = "/serviceAuth/";

    // Method name constants
    public static final String CUSTOM_AUTH_FILTER_METHOD = "CustomAuthenticationFilter";
    public static final String CLIENT_AUTH_SERVICE_METHOD = "ClientAuthenticationService";

    // Git properties constants
    public static final String RESPONSE = "response";
    public static final String GIT_BRANCH = "git.branch";
    public static final String GIT_BUILD_TIME = "git.build.time";
    public static final String GIT_COMMIT_TIME = "git.commit.time";
    public static final String VERSION_INFO_ERROR = "Version information could not be retrieved";

}