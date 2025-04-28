package com.team5.backend.domain.order.dto;

import java.time.LocalDateTime;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderCreateResDto {
	private Long orderId;
	private Long groupId;
	private Long memberId;
	private Integer totalPrice;
	private LocalDateTime createdAt;
	private Integer quantity;
	private OrderStatus status;
	private Integer shipping;

	public static OrderCreateResDto from(Order order) {
		return OrderCreateResDto.builder()
			.orderId(order.getOrderId())
			.groupId(order.getGroupBuy().getGroupBuyId())
			.memberId(order.getMember().getMemberId())
			.totalPrice(order.getTotalPrice())
			.createdAt(order.getCreatedAt())
			.quantity(order.getQuantity())
			.status(OrderStatus.WAITING)
			.shipping(order.getShipping())
			.build();
	}
}

