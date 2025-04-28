package com.team5.backend.domain.notification.dto;

import com.team5.backend.domain.notification.entity.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationCreateReqDto {
    private Long memberId;
    private NotificationType type;
    private String title;
    private String message;
    private String url;
}
