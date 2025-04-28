package com.team5.backend.domain.delivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class DeliveryRequest {

	private String address;
	private String contact;
	private Integer pccc;
}
