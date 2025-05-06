package com.team5.backend.domain.delivery.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryService deliveryService;

	@PostMapping("/order/{orderId}")
	@ResponseStatus(HttpStatus.CREATED)
	public DeliveryResDto createDelivery(
			@PathVariable Long orderId,
			@RequestBody @Valid DeliveryReqDto request
	) {
		Delivery delivery = deliveryService.createDelivery(orderId, request);
		return DeliveryResDto.fromEntity(delivery);
	}

	@GetMapping("/order/{orderId}")
	public DeliveryResDto getDeliveryByOrder(@PathVariable Long orderId) {
		Delivery delivery = deliveryService.getDeliveryByOrder(orderId);
		return DeliveryResDto.fromEntity(delivery);
	}

	@GetMapping("/list")
	public Page<DeliveryResDto> getAllDeliveries(
		@PageableDefault(size = 10) Pageable pageable
	) {
		Page<Delivery> response = deliveryService.getAllDeliveries(pageable);
		return response.map(DeliveryResDto::fromEntity);
	}

	@PatchMapping("/{deliveryId}")
	public DeliveryResDto updateDelivery(
		@PathVariable Long deliveryId,
		@RequestBody @Valid DeliveryReqDto request
	) {
		Delivery delivery = deliveryService.updateDelivery(deliveryId, request);
		return DeliveryResDto.fromEntity(delivery);
	}

	@DeleteMapping("/{deliveryId}")
	public void deleteDelivery(@PathVariable Long deliveryId) {
		deliveryService.deleteDelivery(deliveryId);
	}

}

