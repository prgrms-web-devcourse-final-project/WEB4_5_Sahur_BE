package com.team5.backend.domain.delivery.controller;

import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.delivery.dto.DeliveryRequest;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/delivery")
public class DeliveryController {

	private final DeliveryService deliveryService;

	/**
	 * 특정 배송 정보 수정
	 */
	@PatchMapping("/{deliveryId}")
	public Delivery updateDelivery(
		@PathVariable Long deliveryId,
		@RequestBody DeliveryRequest request
	) {
		return deliveryService.updateDelivery(deliveryId, request);
	}

	/**
	 * 특정 배송 정보 삭제
	 */
	@DeleteMapping("/{deliveryId}")
	public void deleteDelivery(@PathVariable Long deliveryId) {
		deliveryService.deleteDelivery(deliveryId);
	}
}

