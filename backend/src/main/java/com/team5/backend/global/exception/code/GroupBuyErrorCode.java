package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum GroupBuyErrorCode implements ErrorCode {
    GROUP_BUY_NOT_FOUND(404, "GROUP_BUY_NOT_FOUND", "공동구매를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "해당 상품을 찾을 수 없습니다."),
    CATEGORY_NOT_FOUND(404, "CATEGORY_NOT_FOUND", "해당 카테고리를 찾을 수 없습니다."),
    TOKEN_INVALID(401, "TOKEN_INVALID", "유효하지 않은 토큰입니다."),
    TOKEN_BLACKLISTED(401, "TOKEN_BLACKLISTED", "로그아웃된 토큰입니다."),
    GROUP_BUY_ALREADY_CLOSED(401, "ALREADY_CLOSED" ,"이미 종료된 공동구매입니다." );

    private final int status;
    private final String code;
    private final String message;

    GroupBuyErrorCode(int status, String code, String message) {
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
