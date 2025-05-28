package com.team5.backend.domain.notification.template;

public enum NotificationTemplateType {
    PURCHASED,          // 구매 완료
    ORDER_CANCELED,     // 주문 취소
    IN_DELIVERY,        // 배송 중
    DELIVERY_DONE,      // 배송 완료
    REQUEST_APPROVED,   // 요청 승인
    REQUEST_REJECTED,   // 요청 반려
    DIBS_REOPENED,      // 관심 상품 재오픈
    DIBS_DEADLINE,      // 관심 상품 마감 임박
    GROUP_CLOSED        // 모집 종료
}
