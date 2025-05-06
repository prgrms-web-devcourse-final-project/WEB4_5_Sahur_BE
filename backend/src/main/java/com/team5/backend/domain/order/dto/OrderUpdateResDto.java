package com.team5.backend.domain.order.dto;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderUpdateResDto {
	private Long orderId;
	private Integer totalPrice;
	private OrderStatus status;
	private Integer quantity;

	public static OrderUpdateResDto from(Order order) {
		return OrderUpdateResDto.builder()
			.orderId(order.getOrderId())
			.totalPrice(order.getTotalPrice())
			.status(order.getStatus())
			.quantity(order.getQuantity())
			.build();
	}
}
