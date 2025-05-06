package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum HistoryErrorCode implements ErrorCode {
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "회원을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다."),
    GROUP_BUY_NOT_FOUND(404, "GROUP_BUY_NOT_FOUND", "공동구매를 찾을 수 없습니다."),
    HISTORY_NOT_FOUND(404, "HISTORY_NOT_FOUND", "구매 이력을 찾을 수 없습니다."),
    TOKEN_INVALID(401, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    TOKEN_BLACKLISTED(401, "TOKEN_BLACKLISTED", "로그아웃된 토큰입니다.");

    private final int status;
    private final String code;
    private final String message;

    HistoryErrorCode(int status, String code, String message) {
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
