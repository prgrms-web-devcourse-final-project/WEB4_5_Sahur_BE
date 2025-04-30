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
	private String cardCompany;			// 카드사 이름
	private String cardNumberSuffix;	// 카드 번호의 끝 4자리
	private String receiptUrl;			// 결제 영수증 페이지 링크
}
