package com.team5.backend.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class OrderCreateReqDto {
	@NotNull
	private Long memberId;

	@NotNull
	private Long groupBuyId;

	@NotNull
	@Min(1)
	private Integer quantity;
}
