package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.CustomException;
import com.barogo.api.common.exception.ErrorCode;

public class UserNotFoundException extends CustomException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }
}
