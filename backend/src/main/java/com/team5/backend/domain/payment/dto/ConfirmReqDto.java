package com.team5.backend.domain.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConfirmReqDto {

	@NotBlank(message = "paymentKey는 필수입니다.")
	private String paymentKey;

	@NotBlank(message = "orderId는 필수입니다.")
	private String orderId;

	@NotNull(message = "amount는 필수입니다.")
	@Min(value = 100, message = "amount는 최소 100원 이상이어야 합니다.")
	private int amount;
}
