package com.team5.backend.domain.notification.dto;

import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResDto {
    private Long notificationId;
    private Long memberId;
    private NotificationType type;
    private String title;
    private String message;
    private String url;
    private Boolean read;
    private LocalDateTime createdAt;

    public static NotificationResDto fromEntity(Notification notification) {
        return NotificationResDto.builder()
                .notificationId(notification.getNotificationId())
                .memberId(notification.getMember().getMemberId())
                .type(notification.getType())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .url(notification.getUrl())
                .read(notification.getRead())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
