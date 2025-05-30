package com.team5.backend.domain.notification.redis;

import java.util.List;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.team5.backend.domain.notification.template.NotificationTemplateType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class NotificationPublisher {

    @Qualifier("notificationRedisTemplate")
    private final RedisTemplate<String, NotificationEventMessage> notificationRedisTemplate;

    // 구매 완료, 주문 취소, 배송 중, 배송 완료, 요청 승인
    public void publish(NotificationTemplateType type, Long resourceId) {
        publish(type, resourceId, null, null);
    }

    // 요청 반려
    public void publish(NotificationTemplateType type, Long resourceId, String msg) {
        publish(type, resourceId, null, msg);
    }

    // 관심 상품 재오픈, 관심 상품 마감 임박
    public void publish(NotificationTemplateType type, Long resourceId, List<Long> memberIds) {
        publish(type, resourceId, memberIds, null);
    }

    // 공동 구매 종료
    public void publish(NotificationTemplateType type, Long resourceId, List<Long> memberIds, String msg) {
        NotificationEventMessage message = new NotificationEventMessage(type, resourceId, memberIds, msg);

        log.info("[알림 전송] type={}, resourceId={}, memberIds={}, msg={}",
                type, resourceId, memberIds, msg); // 로그 추가

        notificationRedisTemplate.convertAndSend("notification-channel", message);
    }
}
