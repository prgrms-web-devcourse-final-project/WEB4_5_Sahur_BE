package com.team5.backend.domain.payment.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentResDto {
    private String paymentKey;
    private String orderId;
    private String orderName;
    private int totalAmount;
    private String method;            // 결제 수단 (카드, 간편결제)
    private String status;            // 결제 상태 (DONE, CANCELED)
    private String approvedAt;        // 결제 시간

    private String paymentName;        // 카드사나 간편결제 이름
    private String cardNumber;        // 카드 번호
}
