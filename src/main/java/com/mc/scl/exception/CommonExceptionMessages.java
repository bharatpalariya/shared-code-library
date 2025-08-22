package com.mc.scl.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum CommonExceptionMessages {

    SUCCESS(0, "Success"),
    SERVER_ERROR(1, "Internal Server Error, Please Try Again Later."),

    // Authentication Validation Errors
    VALIDATION_ERROR(3, "Invalid Validation Structure Found."),
    UNAUTHORIZED_SERVICE(4, "Service authentication Failed."),
    INVALID_AUTHENTICATION(5,"Invalid Session Found. Please Try After Some Time."),
    IP_ADDRESS_MISSING(6, "You Are Not Allowed To Perform This Action."),
    DATA_NOT_FOUND(7,"Data Not Found.");

    private final int errorCode;
    private final String message;
}
