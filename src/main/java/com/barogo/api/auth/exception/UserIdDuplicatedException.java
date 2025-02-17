package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.CustomException;
import com.barogo.api.common.exception.ErrorCode;

public class UserIdDuplicatedException extends CustomException {

    public UserIdDuplicatedException() {
        super(ErrorCode.USER_ID_DUPLICATED);
    }
}