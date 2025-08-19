package com.mc.scl.exception;

import lombok.Getter;

@Getter
public class CommonException extends RuntimeException {

    private final int errorCode;
    private final String errorMessage;

    public CommonException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public CommonException(CommonExceptionMessages message) {
        super(message.getMessage());
        this.errorCode = message.getErrorCode();
        this.errorMessage = message.getMessage();
    }
}
