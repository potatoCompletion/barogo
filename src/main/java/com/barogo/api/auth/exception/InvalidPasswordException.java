package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.CustomException;
import com.barogo.api.common.exception.ErrorCode;

public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }
}
