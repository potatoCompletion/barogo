package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class UserIdDuplicatedException extends CustomException {

    public UserIdDuplicatedException() {
        super(ErrorCode.USER_ID_DUPLICATED);
    }
}