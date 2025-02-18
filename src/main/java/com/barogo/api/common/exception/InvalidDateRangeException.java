package com.barogo.api.common.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class InvalidDateRangeException extends CustomException {
    public InvalidDateRangeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
