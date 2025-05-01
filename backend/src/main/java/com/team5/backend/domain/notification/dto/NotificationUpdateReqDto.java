package com.team5.backend.domain.notification.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class NotificationUpdateReqDto {
    private Boolean read;
}
