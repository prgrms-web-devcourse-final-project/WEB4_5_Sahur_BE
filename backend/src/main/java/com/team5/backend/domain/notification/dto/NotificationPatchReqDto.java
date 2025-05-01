package com.team5.backend.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NotificationPatchReqDto {
    private Boolean read;
}
