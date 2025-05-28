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

    public void publish(NotificationTemplateType type, Long resourceId) {
        redisTemplate.convertAndSend("notification-channel",
                new NotificationEventMessage(type, resourceId, null, null));
    }

    public void publish(NotificationTemplateType type, Long resourceId, List<Long> memberIds, Long groupBuyId) {
        redisTemplate.convertAndSend("notification-channel",
                new NotificationEventMessage(type, resourceId, memberIds, groupBuyId));
    }
}
