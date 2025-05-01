package com.team5.backend.domain.delivery.controller;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryService deliveryService;

	@PatchMapping("/{deliveryId}")
	public DeliveryResDto updateDelivery(
		@PathVariable Long deliveryId,
		@RequestBody DeliveryReqDto request
	) {
		Delivery delivery = deliveryService.updateDelivery(deliveryId, request);
		return DeliveryResDto.from(delivery);
	}

	@DeleteMapping("/{deliveryId}")
	public void deleteDelivery(@PathVariable Long deliveryId) {
		deliveryService.deleteDelivery(deliveryId);
	}

	@GetMapping("/list")
	public List<DeliveryResDto> getAllDeliveries() {
		return deliveryService.getAllDeliveries()
				.stream()
				.map(DeliveryResDto::from)
				.collect(Collectors.toList());
	}

}

