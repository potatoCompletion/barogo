package com.barogo.api.common.exception.handler;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;
import com.barogo.api.common.exception.base.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // spring validate 실패 (DTO 유효성 검사 실패)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> invalidRequestHandler(MethodArgumentNotValidException e, HttpServletRequest request) {
        List<String> errorList = e.getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
                .path(request.getRequestURI())
                .errorCode(ErrorCode.INVALID_REQUEST_PARAM)
                .errorList(errorList)
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(response.statusCode()).body(response);
    }

    // 커스텀 예외 처리
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> customExceptionHandler(CustomException e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .path(request.getRequestURI())
                .errorCode(e.getErrorCode())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(response.statusCode()).body(response);
    }

    // 예상치 못한 예외 처리 (서버 내부 오류)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> exceptionHandler(Exception e, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
                .path(request.getRequestURI())
                .errorCode(ErrorCode.INTERNAL_SERVER_ERROR)
                .errorList(List.of(e.getMessage()))
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(response.statusCode()).body(response);
    }
}
