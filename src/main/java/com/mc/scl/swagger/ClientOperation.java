package com.mc.scl.swagger;

import com.mc.scl.util.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Custom annotation for Client API operations that require API Key authentication
 * This annotation combines Swagger documentation with security requirements
 */
@Retention(RetentionPolicy.RUNTIME)
@Operation(
    responses = {
        @ApiResponse(
            responseCode = "200", 
            description = "Successful Operation", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = CommonResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - Invalid API Key", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = CommonResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - Access Denied", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = CommonResponse.class)
            )
        )
    },
    security = @SecurityRequirement(name = "apiKey")
)
public @interface ClientOperation {
    
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";

    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "";

    @AliasFor(annotation = Operation.class, attribute = "tags")
    String[] tags() default {};
}