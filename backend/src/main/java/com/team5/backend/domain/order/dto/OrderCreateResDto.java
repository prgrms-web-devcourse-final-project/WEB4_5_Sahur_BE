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
	private Long memberId;
	private Long groupBuyId;
	private Long productId;
	private Integer totalPrice;
	private OrderStatus status;
	private Integer quantity;
	private LocalDateTime createdAt;

	public static OrderCreateResDto from(Order order) {
		return OrderCreateResDto.builder()
			.orderId(order.getOrderId())
			.memberId(order.getMember().getMemberId())
			.groupBuyId(order.getGroupBuy().getGroupBuyId())
			.productId(order.getProduct().getProductId())
			.totalPrice(order.getTotalPrice())
			.status(order.getStatus())
			.quantity(order.getQuantity())
			.createdAt(order.getCreatedAt())
			.build();
	}
}

