package com.team5.backend.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderUpdateReqDto {
	@NotNull(message = "수량은 필수 입력입니다.")
	@Min(value = 1, message = "수량은 1 이상이어야 합니다.")
	private Integer quantity;
}
