package com.team5.backend.domain.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.delivery.dto.CreateDeliveryReq;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final DeliveryService deliveryService;

	@PostMapping
	public Order createOrder(
		@RequestParam Long memberId,
		@RequestParam Long groupBuyId,
		@RequestParam Integer quantity
	) {
		return orderService.createOrder(memberId, groupBuyId, quantity);
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
	public Order updateOrder(
		@PathVariable Long orderId,
		@RequestParam Integer quantity
	) {
		return orderService.updateOrder(orderId, quantity);
	}

	@DeleteMapping("/{orderId}")
	public void cancelOrder(@PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
	}

	@PostMapping("/{orderId}/delivery")
	public Delivery createDelivery(
		@PathVariable Long orderId,
		@RequestBody CreateDeliveryReq request
	) {
		return deliveryService.createDelivery(orderId, request);
	}

	@GetMapping("/{orderId}/delivery")
	public Delivery getDeliveryByOrder(@PathVariable Long orderId) {
		return deliveryService.getDeliveryByOrder(orderId);
	}
}
