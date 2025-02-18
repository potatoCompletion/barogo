package com.barogo.api.delivery.exception;

import com.barogo.api.common.exception.base.CustomException;
import com.barogo.api.common.exception.base.ErrorCode;

public class DeliveryNotFoundException extends CustomException {
    public DeliveryNotFoundException() {
        super(ErrorCode.DELIVERY_NOT_FOUND);
    }
}
