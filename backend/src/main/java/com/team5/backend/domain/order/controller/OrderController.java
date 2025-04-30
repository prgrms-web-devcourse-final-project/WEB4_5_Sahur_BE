package com.team5.backend.domain.order.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import com.team5.backend.domain.order.dto.*;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.service.OrderService;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.RsData;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

	private final OrderService orderService;
	private final DeliveryService deliveryService;
	private final PaymentService paymentService;
	private final TossService tossService;

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

	@PostMapping("/{orderId}/delivery")
	@ResponseStatus(HttpStatus.CREATED)
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

	@GetMapping("/{orderId}/payment")
	public RsData<PaymentResDto> getPaymentByOrder(@PathVariable String orderId) {
		try {
			String paymentKey = paymentService.getPaymentKeyByOrder(orderId);
			PaymentResDto dto = tossService.getPaymentInfoByPaymentKey(paymentKey);
			return new RsData<>("200", "결제 정보를 조회했습니다.", dto);
		} catch (IllegalArgumentException e) {
			return new RsData<>("404-1", "해당 주문의 결제 정보를 찾을 수 없습니다.");
		}
	}
}
