package com.team5.backend.domain.delivery.controller;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

	private final DeliveryService deliveryService;

	@PostMapping("/orders/{orderId}/delivery")
	@ResponseStatus(HttpStatus.CREATED)
	public DeliveryResDto createDelivery(
			@PathVariable Long orderId,
			@RequestBody @Valid DeliveryReqDto request
	) {
		Delivery delivery = deliveryService.createDelivery(orderId, request);
		return DeliveryResDto.fromEntity(delivery);
	}

	@GetMapping("/orders/{orderId}/delivery")
	public DeliveryResDto getDeliveryByOrder(@PathVariable Long orderId) {
		Delivery delivery = deliveryService.getDeliveryByOrder(orderId);
		return DeliveryResDto.fromEntity(delivery);
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

	@GetMapping("/list")
	public List<DeliveryResDto> getAllDeliveries() {
		return deliveryService.getAllDeliveries()
				.stream()
				.map(DeliveryResDto::fromEntity)
				.collect(Collectors.toList());
	}

}

