package com.barogo.api.common.exception.base;

import lombok.Getter;

@Getter
public abstract class CustomException extends RuntimeException{

    private final ErrorCode errorCode;

    public CustomException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}