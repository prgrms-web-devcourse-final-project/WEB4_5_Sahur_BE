package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum DibsErrorCode implements ErrorCode {

    DIBS_NOT_FOUND(404, "DIBS_NOT_FOUND", "관심상품을 찾을 수 없습니다."),
    DIBS_DUPLICATE(409, "DIBS_DUPLICATE", "이미 관심상품에 등록되어 있습니다."),
    DIBS_MEMBER_NOT_FOUND(404, "DIBS_MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    DIBS_PRODUCT_NOT_FOUND(404, "DIBS_PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    DibsErrorCode(int status, String code, String message) {
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
