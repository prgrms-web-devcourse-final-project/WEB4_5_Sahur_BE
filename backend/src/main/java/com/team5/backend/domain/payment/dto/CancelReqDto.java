package com.team5.backend.domain.payment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CancelReqDto {
	@NotBlank(message = "취소 사유는 필수입니다.")
	private String cancelReason;
}
