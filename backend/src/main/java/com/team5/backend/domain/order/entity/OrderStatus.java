package com.team5.backend.domain.order.entity;

public enum OrderStatus {
	BEFOREPAID,	// 결제 대기
	PAID,			// 결제 완료v
	CANCELED,		// 취소
}
