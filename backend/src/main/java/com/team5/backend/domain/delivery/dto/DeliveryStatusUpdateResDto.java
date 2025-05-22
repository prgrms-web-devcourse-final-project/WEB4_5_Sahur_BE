package com.team5.backend.domain.delivery.dto;

import com.team5.backend.domain.delivery.entity.DeliveryStatus;

public record DeliveryStatusUpdateResDto(
        Long deliveryId,
        DeliveryStatus beforeStatus,
        DeliveryStatus afterStatus,
        String resultMessage
) {
}
