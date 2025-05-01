package com.team5.backend.domain.order.controller;

import com.team5.backend.domain.delivery.service.DeliveryService;
import com.team5.backend.domain.order.dto.*;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final DeliveryService deliveryService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
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
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelOrder(@PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
	}

}
