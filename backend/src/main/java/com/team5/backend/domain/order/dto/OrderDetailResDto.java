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
	private Long groupId;
	private String productTitle;
	private String productImage;
	private Long memberId;
	private String nickname;
	private Integer totalPrice;
	private Integer quantity;
	private OrderStatus status;
	private LocalDateTime orderedAt;
	private Integer shippingNumber;
	private DeliveryInfo delivery;

	@Getter
	@Builder
	public static class DeliveryInfo {
		private String address;
		private String contact;
	}

	public static OrderDetailResDto from(Order order) {
		return OrderDetailResDto.builder()
			.orderId(order.getOrderId())
			.groupId(order.getGroupBuy().getGroupBuyId())
			.productTitle(order.getGroupBuy().getProduct().getTitle())
			.productImage(order.getGroupBuy().getProduct().getImageUrl())
			.memberId(order.getMember().getMemberId())
			.nickname(order.getMember().getNickName())
			.totalPrice(order.getTotalPrice())
			.quantity(order.getQuantity())
			.status(order.getStatus())
			.orderedAt(order.getCreatedAt())
			.shippingNumber(order.getShipping())
			.delivery(DeliveryInfo.builder()
				.address(order.getDelivery().getAddress())
				.contact(order.getDelivery().getContact())
				.build())
			.build();
	}
}
