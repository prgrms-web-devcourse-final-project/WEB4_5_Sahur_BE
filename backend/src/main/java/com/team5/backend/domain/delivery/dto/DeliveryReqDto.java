package com.team5.backend.domain.delivery.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReqDto {

	@NotBlank(message = "주소는 필수입니다.")
	private String address;

	@NotBlank(message = "연락처는 필수입니다.")
	private String contact;

	private Integer pccc;
}
