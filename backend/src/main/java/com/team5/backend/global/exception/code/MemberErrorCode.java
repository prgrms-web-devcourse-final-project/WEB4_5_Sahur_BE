package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "MEMBER_001", "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_USED(400, "MEMBER_002", "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_USED(400, "MEMBER_003", "이미 사용 중인 닉네임입니다."),
    EMAIL_NOT_VERIFIED(400, "MEMBER_004", "이메일 인증이 완료되지 않았습니다.");

    private final int status;
    private final String code;
    private final String message;

    MemberErrorCode(int status, String code, String message) {
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