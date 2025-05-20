package com.team5.backend.domain.order.dto;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import lombok.Getter;
import lombok.Builder;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderHistoryInfoDto {
    private Long orderId;
    private Integer totalPrice;
    private Integer quantity;
    private OrderStatus status;
    private LocalDateTime createdAt;

    public static OrderHistoryInfoDto fromEntity(Order order) {
        return OrderHistoryInfoDto.builder()
                .orderId(order.getOrderId())
                .totalPrice(order.getTotalPrice())
                .quantity(order.getQuantity())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}
