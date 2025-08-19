package com.mc.scl.exception;

import com.mc.scl.logger.AppLogger;
import com.mc.scl.logger.model.LogLayout;
import com.mc.scl.util.CommonResponse;
import com.mc.scl.util.VariablesConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class CommonExceptionHandler extends ResponseEntityExceptionHandler {
    private static final Logger exLogger = LogManager.getLogger(CommonExceptionHandler.class);
    
    private final HttpServletRequest request;
    private final AppLogger appLogger;

    public CommonExceptionHandler(HttpServletRequest request, AppLogger appLogger) {
        this.request = request;
        this.appLogger = appLogger;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseEntity<CommonResponse> handleGlobalException(Exception ex) {
        logException(ex, VariablesConstant.ERROR);
        CommonResponse response = new CommonResponse(CommonExceptionMessages.SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CommonException.class)
    @ResponseBody
    public ResponseEntity<CommonResponse> handleCommonException(CommonException ex) {
        // Log business exceptions at WARN level as they are handled, not system errors
        logException(ex, VariablesConstant.WARNING);
        CommonResponse response = new CommonResponse(ex.getErrorCode(), ex.getErrorMessage(), null);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    @ResponseBody
    public ResponseEntity<CommonResponse> handleBindException(BindException ex) {
        logException(ex, VariablesConstant.WARNING);
        CommonResponse response = new CommonResponse(CommonExceptionMessages.VALIDATION_ERROR);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseBody
    public ResponseEntity<CommonResponse> handleGlobalException(RuntimeException ex) {
        logException(ex, VariablesConstant.WARNING);
        CommonResponse response = new CommonResponse(CommonExceptionMessages.SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * Common logging method to handle exception logging with proper error handling
     */
    private void logException(Exception ex, String logLevel) {
        try {
            // Safe stack trace access
            String className = "";
            String methodName = "";
            if (ex.getStackTrace() != null && ex.getStackTrace().length > 0) {
                className = ex.getStackTrace()[0].getClassName();
                methodName = ex.getStackTrace()[0].getMethodName();
            }

            LogLayout logLayout = new LogLayout(logLevel, VariablesConstant.EXCEPTION, VariablesConstant.RUNTIME,
                    className, methodName, "", "", "", "error_from_exception_handler");
            appLogger.writeLog(logLayout, ex, request);
            
        } catch (Exception e) {
            // Fallback logging - never let logging failure break exception handling
            exLogger.error("Failed to log exception through AppLogger: {}", e.getMessage());
            exLogger.error("Original exception: {}", ex.getMessage(), ex);
            exLogger.error("Logging failure: {}", e.getMessage(), e);
        }
    }
}
