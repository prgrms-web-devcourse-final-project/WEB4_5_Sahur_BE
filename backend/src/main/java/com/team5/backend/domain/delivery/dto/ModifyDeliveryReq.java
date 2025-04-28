package com.team5.backend.domain.delivery.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ModifyDeliveryReq {

	private String address;
	private Integer contact;
	private Integer pccc;
}
