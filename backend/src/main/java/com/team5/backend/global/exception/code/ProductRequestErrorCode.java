package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum ProductRequestErrorCode implements ErrorCode {

    PRODUCT_REQUEST_NOT_FOUND(404, "PRODUCT_REQUEST_NOT_FOUND", "해당 상품 요청을 찾을 수 없습니다."),
    PRODUCT_REQUEST_ACCESS_DENIED(403, "PRODUCT_REQUEST_ACCESS_DENIED", "해당 상품 요청에 대한 접근 권한이 없습니다."),
    CATEGORY_NOT_FOUND(404, "CATEGORY_NOT_FOUND", "해당 카테고리를 찾을 수 없습니다."),
    INVALID_PRODUCT_REQUEST_STATUS(400, "INVALID_PRODUCT_REQUEST_STATUS", "유효하지 않은 상품 등록 요청 상태입니다."),
    PRODUCT_IMAGE_NOT_FOUND(400, "PRODUCT_IMAGE_NOT_FOUND", "상품 이미지는 최소 1개 이상 업로드해야 합니다.");

    private final int status;
    private final String code;
    private final String message;

    ProductRequestErrorCode(int status, String code, String message) {
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
