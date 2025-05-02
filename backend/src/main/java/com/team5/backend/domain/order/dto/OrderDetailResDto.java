package com.team5.backend.domain.order.dto;

import java.time.LocalDateTime;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class OrderDetailResDto {

	private Long orderId;
	private Long memberId;
	private String nickname;
	private Long groupBuyId;
	private Long productId;
	private String productTitle;
	private String productImage;
	private Integer totalPrice;
	private OrderStatus status;
	private Integer quantity;
	private LocalDateTime createdAt;

	public static OrderDetailResDto from(Order order) {
		return OrderDetailResDto.builder()
			.orderId(order.getOrderId())
			.memberId(order.getMember().getMemberId())
			.nickname(order.getMember().getNickname())
			.groupBuyId(order.getGroupBuy().getGroupBuyId())
			.productId(order.getProduct().getProductId())
			.productTitle(order.getProduct().getTitle())
			.productImage(order.getProduct().getImageUrl())
			.totalPrice(order.getTotalPrice())
			.status(order.getStatus())
			.quantity(order.getQuantity())
			.createdAt(order.getCreatedAt())
			.build();
	}
}
