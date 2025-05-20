package com.team5.backend.global.exception.code;

import com.team5.backend.global.exception.ErrorCode;

public enum PaymentErrorCode implements ErrorCode {
    INVALID_CARD_CODE(400, "INVALID_CARD_CODE", "유효하지 않은 카드 코드입니다."),
    ORDER_NOT_FOUND(404, "ORDER_NOT_FOUND", "주문을 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND(404, "PAYMENT_NOT_FOUND", "결제를 찾을 수 없습니다."),
    PAYMENT_NOT_FOUND_BY_ORDER(404, "PAYMENT_NOT_FOUND_BY_ORDER", "해당 주문의 결제가 존재하지 않습니다."),
    TOSS_CONFIRM_FAILED(502, "TOSS_CONFIRM_FAILED", "Toss 결제 승인 요청에 실패했습니다."),
    TOSS_FETCH_FAILED(502, "TOSS_FETCH_FAILED", "Toss 결제 정보 조회에 실패했습니다."),
    TOSS_CANCEL_FAILED(502, "TOSS_CANCEL_FAILED", "Toss 결제 취소 요청에 실패했습니다.");

    private final int status;
    private final String code;
    private final String message;

    PaymentErrorCode(int status, String code, String message) {
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
