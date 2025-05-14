package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum OrderErrorCode implements ErrorCode {
    ORDER_NOT_FOUND(404, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    INVALID_ORDER_STATUS(400, "INVALID_ORDER_STATUS", "유효하지 않은 주문 상태입니다."),
    OUT_OF_STOCK(409, "OUT_OF_STOCK", "재고가 부족합니다."),
    MEMBER_NOT_FOUND(404, "MEMBER_NOT_FOUND", "회원 정보를 찾을 수 없습니다."),
    GROUPBUY_NOT_FOUND(404, "GROUPBUY_NOT_FOUND", "공동구매 정보를 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(404, "PRODUCT_NOT_FOUND", "상품 정보를 찾을 수 없습니다."),
    ORDER_ALREADY_CANCELED(400, "ORDER_ALREADY_CANCELED", "이미 취소된 주문입니다.");

    private final int status;
    private final String code;
    private final String message;

    OrderErrorCode(int status, String code, String message) {
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