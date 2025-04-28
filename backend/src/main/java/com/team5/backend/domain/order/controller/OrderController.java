package com.team5.backend.domain.order.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
