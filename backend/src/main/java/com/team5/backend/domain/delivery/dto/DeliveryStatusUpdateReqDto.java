package com.team5.backend.domain.delivery.dto;

import java.util.List;

import jakarta.validation.constraints.NotNull;

public record DeliveryStatusUpdateReqDto(
        @NotNull(message = "배송 ID 리스트는 필수입니다.")
        List<@NotNull Long> deliveryIds
) {
}
