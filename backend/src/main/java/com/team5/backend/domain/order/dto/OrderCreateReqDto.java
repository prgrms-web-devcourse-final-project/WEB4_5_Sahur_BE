package com.team5.backend.domain.order.dto;

import lombok.Getter;

@Getter
public class OrderCreateReqDto {
	private Long memberId;
	private Long groupBuyId;
	private Integer quantity;
}
