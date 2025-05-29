package com.team5.backend.domain.notification.redis;

import java.io.Serializable;
import java.util.List;

import com.team5.backend.domain.notification.template.NotificationTemplateType;

public record NotificationEventMessage(
        NotificationTemplateType type,
        Long resourceId,
        List<Long> memberIds,
        Long groupBuyId,
        String adminMessage
) implements Serializable {
}
