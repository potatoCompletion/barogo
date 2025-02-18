package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class InvalidCredentialException extends CustomException {

    public InvalidCredentialException() {
        super(ErrorCode.INVALID_CREDENTIAL);
    }
}
