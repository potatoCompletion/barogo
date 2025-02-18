package com.barogo.api.auth.jwt;

import com.barogo.api.auth.exception.InvalidJwtException;
import com.barogo.api.common.exception.base.ErrorCode;
import com.barogo.api.common.exception.base.ErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@RequiredArgsConstructor
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        Object exception = request.getAttribute("exception");
        ErrorCode errorCode = (exception instanceof InvalidJwtException)
                ? ((InvalidJwtException) exception).getErrorCode()
                : ErrorCode.UNAUTHORIZED; // 기본적으로 UNAUTHORIZED 처리

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getStatus().value());
        response.getWriter().write(
                objectMapper.writeValueAsString(
                        ErrorResponse.builder()
                                .path(request.getRequestURI())
                                .errorCode(errorCode)
                                .timestamp(LocalDateTime.now())
                                .build()
                )
        );
        response.getWriter().flush();
    }
}
