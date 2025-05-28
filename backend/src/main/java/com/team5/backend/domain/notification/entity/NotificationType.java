package com.team5.backend.domain.notification.entity;

public enum NotificationType {
    ORDER,          // 주문, 배송 관련
    REQUEST,        // 상품 요청 승인/반려
    GROUP_BUY,      // 공동구매 종료
    DIBS,           // 관심상품 알림
    SYSTEM          // 그 외 시스템 공지 등
}
