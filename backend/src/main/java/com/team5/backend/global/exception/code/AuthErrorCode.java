package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum AuthErrorCode implements ErrorCode {

    INVALID_LOGIN_INFO(401, "INVALID_LOGIN_INFO", "이메일 또는 비밀번호가 일치하지 않습니다."),
    ACCESS_TOKEN_NOT_FOUND(401, "ACCESS_TOKEN_NOT_FOUND", "액세스 토큰이 없습니다."),
    INVALID_REFRESH_TOKEN(401, "INVALID_REFRESH_TOKEN", "유효하지 않은 리프레시 토큰입니다."),
    TOKEN_MISMATCH(401, "TOKEN_MISMATCH", "토큰이 일치하지 않습니다."),
    LOGOUT_TOKEN(401, "LOGOUT_TOKEN", "로그아웃된 토큰입니다."),
    INVALID_TOKEN(401, "INVALID_TOKEN", "유효하지 않은 토큰입니다."),
    EXPIRED_TOKEN(401, "EXPIRED_TOKEN", "만료된 토큰입니다."),
    REFRESH_TOKEN_NOT_FOUND(401, "REFRESH_TOKEN_NOT_FOUND", "리프레시 토큰이 존재하지 않습니다."),
    UNAUTHORIZED(401, "UNAUTHORIZED", "로그인이 필요합니다."),
    FORBIDDEN(403, "FORBIDDEN", "접근 권한이 없습니다.");

    private final int status;
    private final String code;
    private final String message;

    AuthErrorCode(int status, String code, String message) {
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
