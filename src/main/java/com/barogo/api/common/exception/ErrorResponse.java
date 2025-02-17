package com.barogo.api.common.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(int statusCode, String errorCode, String message, List<String> errorList) {

    @Builder
    public ErrorResponse(ErrorCode errorCode, List<String> errorList) {
        this(errorCode.getStatusCode(), errorCode.name(), errorCode.getMessage(), errorList);
    }
}