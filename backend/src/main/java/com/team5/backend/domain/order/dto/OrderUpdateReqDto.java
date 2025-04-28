package com.team5.backend.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderUpdateReqDto {
	private Integer quantity;
	private DeliveryInfo delivery;

	@Getter
	public static class DeliveryInfo {
		private String address;
		private String contact;
	}
}
