package com.barogo.api.common.exception.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime timestamp,
        String path,
        int statusCode,
        String errorCode,
        String message,
        List<String> errorList
) {

    @Builder
    public ErrorResponse(ErrorCode errorCode, String path, LocalDateTime timestamp, List<String> errorList) {
        this(timestamp, path, errorCode.getStatusCode(), errorCode.name(), errorCode.getMessage(), errorList);
    }
}