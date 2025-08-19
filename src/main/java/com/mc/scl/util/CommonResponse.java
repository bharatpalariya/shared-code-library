package com.mc.scl.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mc.scl.exception.CommonExceptionMessages;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CommonResponse implements Serializable {

    private int errorCode;
    private String errorMessage;
    private transient Object data;

    public CommonResponse(CommonExceptionMessages message) {
        this.errorCode = message.getErrorCode();
        this.errorMessage = message.getMessage();
    }
    
    public CommonResponse(CommonExceptionMessages message, Object data) {
        this.errorCode = message.getErrorCode();
        this.errorMessage = message.getMessage();
        this.data = data;
    }
}
