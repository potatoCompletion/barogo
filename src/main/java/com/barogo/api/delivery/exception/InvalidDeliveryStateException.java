package com.barogo.api.delivery.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class InvalidDeliveryStateException extends CustomException {

    public InvalidDeliveryStateException() {
        super(ErrorCode.INVALID_DELIVERY_STATE);
    }
}
