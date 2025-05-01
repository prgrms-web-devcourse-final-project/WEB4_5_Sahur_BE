package com.team5.backend.domain.notification.dto;

import com.team5.backend.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationCreateReqDto {
    @NotNull
    private Long memberId;
    @NotNull
    private NotificationType type;
    @NotNull
    private String title;
    @NotNull
    private String message;
    @NotNull
    private String url;
}
