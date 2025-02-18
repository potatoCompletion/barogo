package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class InvalidJwtException extends CustomException {
    public InvalidJwtException(ErrorCode errorCode) {
        super(errorCode);
    }
}
