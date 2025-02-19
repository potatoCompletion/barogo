package com.barogo.api.auth.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class PasswordPolicyViolationException extends CustomException {

    public PasswordPolicyViolationException() {
        super(ErrorCode.PASSWORD_POLICY_VIOLATION);
    }
}
