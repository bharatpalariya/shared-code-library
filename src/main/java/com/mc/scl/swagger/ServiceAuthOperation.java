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
 * Custom annotation for Service Auth API operations that require Service Code and Service Auth Key authentication
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
            responseCode = "400", 
            description = "Bad Request - Malformed or missing Authorization header", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = CommonResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "401", 
            description = "Unauthorized - Invalid Service Code or Service Auth Key in Basic Authentication", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = CommonResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "403", 
            description = "Forbidden - IP address not in whitelist or access denied", 
            content = @Content(
                mediaType = "application/json", 
                schema = @Schema(implementation = CommonResponse.class)
            )
        )
    },
    security = {
        @SecurityRequirement(name = "serviceCode"),
        @SecurityRequirement(name = "ServiceAuthKey")
    }
)
public @interface ServiceAuthOperation {
    
    @AliasFor(annotation = Operation.class, attribute = "summary")
    String summary() default "";
    
    @AliasFor(annotation = Operation.class, attribute = "description")
    String description() default "";
    
    @AliasFor(annotation = Operation.class, attribute = "tags")
    String[] tags() default {};
}