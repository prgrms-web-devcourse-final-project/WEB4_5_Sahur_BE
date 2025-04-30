package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_FOUND(404, "PRODUCT_001", "상품을 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    ProductErrorCode(int status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    @Override
    public int getStatus() { return status; }
    @Override
    public String getCode() { return code; }
    @Override
    public String getMessage() { return message; }
}
