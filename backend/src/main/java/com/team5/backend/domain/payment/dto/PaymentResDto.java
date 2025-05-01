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
	private String method;      		// 결제 수단 (ex. 카드)
	private String status;      		// 결제 상태 (DONE, CANCELED)
	private String approvedAt;  		// 결제 승인 시간
	private String issuerCode;
	private String acquirerCode;
	private String cardNumber;			// 카드 번호
}
