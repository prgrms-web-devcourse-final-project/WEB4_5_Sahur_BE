package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum CommonErrorCode implements ErrorCode {
    VALIDATION_ERROR(400, "VALIDATION_ERROR", "요청값이 올바르지 않습니다."),
    UNAUTHORIZED(403, "UNAUTHORIZED", "접근 권한이 없습니다."),
    INTERNAL_ERROR(500, "INTERNAL_ERROR", "서버 내부 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    CommonErrorCode(int status, String code, String message) {
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