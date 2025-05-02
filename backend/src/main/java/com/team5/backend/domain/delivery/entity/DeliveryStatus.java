package com.team5.backend.domain.delivery.entity;

public enum DeliveryStatus {
    PREPARING,    // 배송 준비
    INDELIVERY,   // 배송 중
    COMPLETED,    // 배송 완료
    CANCELED      // 배송 취소
}
