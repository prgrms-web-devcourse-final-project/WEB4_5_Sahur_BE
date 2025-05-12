package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum DeliveryErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(404, "DELIVERY_001", "주문을 찾을 수 없습니다."),
    DELIVERY_NOT_FOUND(404, "DELIVERY_002", "배송 정보를 찾을 수 없습니다."),
    INVALID_STATUS_TRANSITION(400, "DELIVERY_003", "잘못된 배송 상태 전이입니다.");

    private final int status;
    private final String code;
    private final String message;

    DeliveryErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
