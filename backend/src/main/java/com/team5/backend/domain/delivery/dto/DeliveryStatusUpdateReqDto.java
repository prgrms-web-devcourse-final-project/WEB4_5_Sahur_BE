package com.team5.backend.domain.delivery.dto;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public record DeliveryStatusUpdateReqDto(
        @NotNull(message = "배송 ID 리스트는 필수입니다.")
        List<@NotNull Long> orderIds
) {
}
