package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum NotificationErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    NOTIFICATION_NOT_FOUND(404, "NOTIFICATION_NOT_FOUND", "알림을 찾을 수 없습니다."),
    TOKEN_INVALID(401, "NOTIFICATION_TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    TOKEN_BLACKLISTED(401, "NOTIFICATION_TOKEN_BLACKLISTED", "로그아웃된 토큰입니다.");

    private final int status;
    private final String code;
    private final String message;

    NotificationErrorCode(int status, String code, String message) {
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
