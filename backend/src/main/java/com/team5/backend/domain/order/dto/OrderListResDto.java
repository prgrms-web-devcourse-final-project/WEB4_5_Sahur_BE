package com.team5.backend.domain.order.dto;

import java.time.LocalDateTime;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderListResDto {
	private Long orderId;
	private Long memberId;
	private String nickname;
	private Long groupId;
	private String productTitle;
	private Integer totalPrice;
	private OrderStatus status;
	private Integer quantity;
	private LocalDateTime createdAt;

	public static OrderListResDto from(Order order) {
		return OrderListResDto.builder()
			.orderId(order.getOrderId())
			.memberId(order.getMember().getMemberId())
			.nickname(order.getMember().getNickname())
			.groupId(order.getGroupBuy().getGroupBuyId())
			.productTitle(order.getGroupBuy().getProduct().getTitle())
			.totalPrice(order.getTotalPrice())
			.status(order.getStatus())
			.quantity(order.getQuantity())
			.createdAt(order.getCreatedAt())
			.build();
	}
}