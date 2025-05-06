package com.team5.backend.domain.order.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderCreateReqDto {
	@NotNull(message = "회원 ID는 필수 입력입니다.")
	private Long memberId;

	@NotNull(message = "공동구매 ID는 필수 입력입니다.")
	private Long groupBuyId;

	@NotNull(message = "상품 ID는 필수 입력입니다.")
	private Long productId;

	@NotNull(message = "수량은 필수 입력입니다.")
	@Min(value = 1, message = "수량은 1 이상이어야 합니다.")
	private Integer quantity;
}
