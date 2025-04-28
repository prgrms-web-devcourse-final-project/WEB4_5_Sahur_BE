package com.team5.backend.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderUpdateReqDto {
	@NotNull
	@Min(1)
	private Integer quantity;

	private DeliveryInfo delivery;

	@Getter
	public static class DeliveryInfo {
		private String address;
		private String contact;
	}
}
