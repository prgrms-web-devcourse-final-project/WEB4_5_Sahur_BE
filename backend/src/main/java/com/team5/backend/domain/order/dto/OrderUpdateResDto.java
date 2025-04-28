package com.team5.backend.domain.order.dto;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderUpdateResDto {
	private Long orderId;
	private Integer quantity;
	private Integer totalPrice;
	private OrderStatus status;
	private DeliveryInfo delivery;

	@Getter
	@Builder
	public static class DeliveryInfo {
		private String address;
		private String contact;
	}

	public static OrderUpdateResDto from(Order order) {
		return OrderUpdateResDto.builder()
			.orderId(order.getOrderId())
			.quantity(order.getQuantity())
			.totalPrice(order.getTotalPrice())
			.status(order.getStatus())
			.delivery(order.getDelivery() != null
				? DeliveryInfo.builder()
				.address(order.getDelivery().getAddress())
				.contact(order.getDelivery().getContact())
				.build()
				: null
			)
			.build();
	}
}
