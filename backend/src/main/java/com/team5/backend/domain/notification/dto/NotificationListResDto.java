package com.team5.backend.domain.notification.dto;

import org.springframework.data.domain.Page;

public record NotificationListResDto(
        Page<NotificationResDto> notifications,
        long unreadCount
) {
    public static NotificationListResDto of(Page<NotificationResDto> notifications, long unreadCount) {
        return new NotificationListResDto(notifications, unreadCount);
    }
}