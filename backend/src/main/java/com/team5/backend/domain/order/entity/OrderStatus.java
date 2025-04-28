package com.team5.backend.domain.order.entity;

public enum OrderStatus {
	WAITING,		// 결제 대기 중
	PAID,			// 결제 완료
	CANCELED,		// 취소
	PREPARING,		// 배송 준비
	COMPLETED,		// 배송 완료
}
