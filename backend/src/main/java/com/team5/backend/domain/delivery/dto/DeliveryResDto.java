package com.team5.backend.domain.delivery.dto;

import com.team5.backend.domain.delivery.entity.Delivery;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryResDto {

	private String address;
	private String contact;
	private Integer pccc;

	public static DeliveryResDto from(Delivery delivery) {
		return DeliveryResDto.builder()
			.address(delivery.getAddress())
			.contact(delivery.getContact())
			.pccc(delivery.getPccc())
			.build();
	}
}
