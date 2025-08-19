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
    UNAUTHORIZED_CLIENT(4, "Client authentication failed. Invalid API key"),
    UNAUTHORIZED_USER(5, "Session Expired! Please Try After Some Time..");

    private final int errorCode;
    private final String message;
}
