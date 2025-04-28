package com.team5.backend.domain.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.service.OrderService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;

	@PostMapping
	public Order createOrder(@RequestBody OrderCreateReqDto request) {
		return orderService.createOrder(request);
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
		@RequestBody OrderUpdateReqDto request
	) {
		return orderService.updateOrder(orderId, request);
	}

	@DeleteMapping("/{orderId}")
	public void cancelOrder(@PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
	}
}
