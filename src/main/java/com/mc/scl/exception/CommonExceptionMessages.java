package com.mc.scl.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionMessages {

    SUCCESS(0, "Success"),
    SERVER_ERROR(1, "Internal Server Error, Please try again later."),

    // Authentication Validation Errors
    VALIDATION_ERROR(3, "Invalid Validation Structure Found."),
    UNAUTHORIZED_SERVICE(4, "Service authentication failed."),
    INVALID_SERVICE_CODE(5, "Invalid Service Code Found."),
    INVALID_AUTH_KEY(6, "Invalid Auth Key Found."),
    IP_ADDRESS_MISSING(7, "Please Provide Valid System IP Address.");

    private final int errorCode;
    private final String message;
}
