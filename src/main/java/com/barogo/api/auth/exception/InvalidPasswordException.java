package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class InvalidPasswordException extends CustomException {

    public InvalidPasswordException() {
        super(ErrorCode.INVALID_PASSWORD);
    }
}
