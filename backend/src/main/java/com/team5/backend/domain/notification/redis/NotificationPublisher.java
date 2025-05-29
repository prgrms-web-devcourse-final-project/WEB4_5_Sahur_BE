package com.team5.backend.domain.notification.redis;

import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.team5.backend.domain.notification.template.NotificationTemplateType;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class NotificationPublisher {

    private final RedisTemplate<String, Object> redisTemplate;

    // 구매 완료, 주문 취소, 배송 중, 배송 완료, 요청 승인
    public void publish(NotificationTemplateType type, Long resourceId) {
        publish(type, resourceId, null, null, null);
    }

    // 요청 반려
    public void publish(NotificationTemplateType type, Long resourceId, String msg) {
        publish(type, resourceId, null, null, msg);
    }

    // 관심 상품 재오픈, 관심 상품 마감 임박
    public void publish(NotificationTemplateType type, Long resourceId, List<Long> memberIds, Long groupBuyId) {
        publish(type, resourceId, memberIds, groupBuyId, null);
    }

    // 공동 구매 종료
    public void publish(NotificationTemplateType type, Long resourceId, List<Long> memberIds, String msg) {
        publish(type, resourceId, memberIds, null, msg);
    }

    public void publish(NotificationTemplateType type, Long resourceId, List<Long> memberIds, Long groupBuyId, String msg) {
        NotificationEventMessage message = new NotificationEventMessage(type, resourceId, memberIds, groupBuyId, msg);
        redisTemplate.convertAndSend("notification-channel", message);
    }
}
