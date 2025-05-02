package com.team5.backend.domain.delivery.dto;

import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DeliveryResDto {

	private String address;
	private Integer pccc;
	private String contact;
	private DeliveryStatus status;
	private String shipping;

	public static DeliveryResDto fromEntity(Delivery delivery) {
		return DeliveryResDto.builder()
			.address(delivery.getAddress())
			.pccc(delivery.getPccc() != null ? delivery.getPccc() : 0)
			.contact(delivery.getContact())
			.status(delivery.getStatus())
			.shipping(delivery.getShipping())
			.build();
	}
}
