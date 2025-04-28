package com.team5.backend.domain.order.dto;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CreateOrderResDto {
	private Long orderId;
	private Long groupId;
	private Long memberId;
	private Integer totalPrice;
	private LocalDateTime createdAt;
	private Integer quantity;
	private OrderStatus status;
	private Integer shipping;

	public static CreateOrderResDto from(Order order) {
		return CreateOrderResDto.builder()
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

