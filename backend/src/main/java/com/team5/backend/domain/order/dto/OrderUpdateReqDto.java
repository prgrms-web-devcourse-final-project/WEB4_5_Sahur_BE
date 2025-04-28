package com.team5.backend.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
		@NotBlank(message = "주소는 필수 입력 항목입니다.")
		private String address;

		@NotBlank(message = "연락처는 필수 입력 항목입니다.")
		private String contact;
	}
}
