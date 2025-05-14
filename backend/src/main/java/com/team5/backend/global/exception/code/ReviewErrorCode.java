package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum ReviewErrorCode implements ErrorCode {
    REVIEW_NOT_FOUND(404, "REVIEW_NOT_FOUND", "리뷰를 찾을 수 없습니다."),
    DUPLICATE_REVIEW(409, "DUPLICATE_REVIEW", "이미 리뷰를 작성했습니다."),
    INVALID_REVIEW_CONTENT(400, "INVALID_REVIEW_CONTENT", "리뷰 내용이 유효하지 않습니다."),
    TOKEN_INVALID(401, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    TOKEN_BLACKLISTED(401, "TOKEN_BLACKLISTED", "로그아웃된 토큰입니다.");

    private final int status;
    private final String code;
    private final String message;

    ReviewErrorCode(int status, String code, String message) {
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
