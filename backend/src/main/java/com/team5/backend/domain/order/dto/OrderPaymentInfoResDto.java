package com.team5.backend.domain.order.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderPaymentInfoResDto {
    private Long orderId;
    private String orderName;
    private Integer amount;
    private String clientKey;
}
