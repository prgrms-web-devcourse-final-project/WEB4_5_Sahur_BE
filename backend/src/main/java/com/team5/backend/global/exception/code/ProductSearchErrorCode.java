package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum ProductSearchErrorCode implements ErrorCode {
    SEARCH_FAILED(500, "PRODUCT_SEARCH.FAILED", "Elasticsearch 검색 중 오류가 발생했습니다.");

    private final int status;
    private final String code;
    private final String message;

    ProductSearchErrorCode(int status, String code, String message) {
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
