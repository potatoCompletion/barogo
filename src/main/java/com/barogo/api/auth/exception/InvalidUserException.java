package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class InvalidUserException extends CustomException {
    public InvalidUserException() {
        super(ErrorCode.INVALID_USER);
    }
}
