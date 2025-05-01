package com.team5.backend.domain.notification.dto;

import com.team5.backend.domain.notification.entity.NotificationType;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class NotificationUpdateReqDto {
    @NotNull
    private NotificationType type;
    @NotNull
    private String title;
    @NotNull
    private String message;
    @NotNull
    private String url;
    @NotNull
    private Boolean read;
}
