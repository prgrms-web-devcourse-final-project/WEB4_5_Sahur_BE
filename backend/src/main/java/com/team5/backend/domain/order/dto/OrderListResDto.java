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
	private Long groupId;
	private String groupTitle; // 상품명
	private Long memberId;
	private String nickname;   // 회원 닉네임
	private Integer totalPrice;
	private Integer quantity;
	private OrderStatus status;
	private LocalDateTime createdAt;

	public static OrderListResDto from(Order order) {
		return OrderListResDto.builder()
			.orderId(order.getOrderId())
			.groupId(order.getGroupBuy().getGroupBuyId())
			.groupTitle(order.getGroupBuy().getProduct().getTitle())
			.memberId(order.getMember().getMemberId())
			.nickname(order.getMember().getNickName())
			.totalPrice(order.getTotalPrice())
			.quantity(order.getQuantity())
			.status(order.getStatus())
			.createdAt(order.getCreatedAt())
			.build();
	}
}
