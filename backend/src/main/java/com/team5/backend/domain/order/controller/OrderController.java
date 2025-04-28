package com.team5.backend.domain.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.order.dto.*;
import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final DeliveryService deliveryService;

	@PostMapping
	public OrderCreateResDto createOrder(@RequestBody OrderCreateReqDto request) {
		Order order = orderService.createOrder(request);
		return OrderCreateResDto.from(order);
	}

	@GetMapping
	public List<OrderListResDto> getOrders() {
		return orderService.getOrders();
	}

	@GetMapping("/{orderId}")
	public OrderDetailResDto getOrderDetail(@PathVariable Long orderId) {
		return orderService.getOrderDetail(orderId);
	}

	@PatchMapping("/{orderId}")
	public OrderUpdateResDto updateOrder(
		@PathVariable Long orderId,
		@RequestBody OrderUpdateReqDto request
	) {
		Order order = orderService.updateOrder(orderId, request);
		return OrderUpdateResDto.from(order);
	}

	@DeleteMapping("/{orderId}")
	public void cancelOrder(@PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
	}

	@PostMapping("/{orderId}/delivery")
	public DeliveryResDto createDelivery(
		@PathVariable Long orderId,
		@RequestBody DeliveryReqDto request
	) {
		if (orderId == null) {
			throw new IllegalArgumentException("orderId는 필수 입력값입니다.");
		}

		Delivery delivery = deliveryService.createDelivery(orderId, request);
		return DeliveryResDto.from(delivery);
	}

	@GetMapping("/{orderId}/delivery")
	public DeliveryResDto getDeliveryByOrder(@PathVariable Long orderId) {
		Delivery delivery = deliveryService.getDeliveryByOrder(orderId);
		return DeliveryResDto.from(delivery);
	}
}
