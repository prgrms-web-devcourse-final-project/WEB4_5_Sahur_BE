package com.team5.backend.domain.delivery.dto;

import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReqDto {

	@NotBlank(message = "주소는 필수입니다.")
	private String address;

	private Integer pccc;

	@NotBlank(message = "연락처는 필수입니다.")
	private String contact;

	@NotNull(message = "배송 상태는 필수입니다.")
	private DeliveryStatus status;

	@NotBlank(message = "운송장 정보는 필수입니다.")
	private String shipping;
}
