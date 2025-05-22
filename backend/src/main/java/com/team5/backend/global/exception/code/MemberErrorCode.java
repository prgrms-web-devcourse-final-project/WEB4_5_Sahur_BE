package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum MemberErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    EMAIL_ALREADY_USED(400, "EMAIL_ALREADY_USED", "이미 사용 중인 이메일입니다."),
    NICKNAME_ALREADY_USED(400, "NICKNAME_ALREADY_USED", "이미 사용 중인 닉네임입니다."),
    EMAIL_NOT_VERIFIED(400, "EMAIL_NOT_VERIFIED", "이메일 인증이 완료되지 않았습니다."),
    MEMBER_NOT_DELETED(400, "MEMBER_NOT_DELETED", "삭제된 계정이 아닙니다."),
    INVALID_EMAIL_DOMAIN(400, "INVALID_EMAIL_DOMAIN", "유효하지 않은 이메일 주소입니다."),
    ADMIN_ONLY(403, "ADMIN_ONLY", "관리자 권한이 필요합니다.");

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