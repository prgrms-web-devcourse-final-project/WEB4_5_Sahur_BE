package com.team5.backend.domain.payment.entity;

/**
 * 결제 상태를 나타내는 Enum.
 * - REQUESTED: 결제 요청됨
 * - CONFIRMED: 결제 완료됨
 * - CANCELED: 결제 취소됨
 */
public enum PaymentStatus {
	REQUESTED,
	CONFIRMED,
	CANCELED
}
