package com.mc.scl.logger.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data structure for holding log entry information.
 * This class is used to reduce the number of parameters passed to logging methods.
 * Exception is handled separately to avoid potential serialization issues.
 */
@Getter
@AllArgsConstructor
public class LogLayout {
    
    private final String logLevel;
    private final String logName;
    private final String module;
    private final String className;
    private final String methodName;
    private final String data1;
    private final String data2;
    private final String additionalData;
    private final String message;
}