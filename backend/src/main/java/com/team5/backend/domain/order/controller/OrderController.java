package com.team5.backend.domain.order.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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

import jakarta.validation.Valid;
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
	public RsData<OrderCreateResDto> createOrder(@RequestBody OrderCreateReqDto request) {
		Order order = orderService.createOrder(request);
		return new RsData<>("201", "주문이 성공적으로 생성되었습니다.", OrderCreateResDto.from(order));
	}

	@GetMapping
	public RsData<Page<OrderListResDto>> getOrders(
		@PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
	) {
		Page<OrderListResDto> orderDtos = orderService.getOrders(pageable)
			.map(OrderListResDto::from);
		return new RsData<>("200", "주문 목록 조회에 성공했습니다.", orderDtos);
	}

	@GetMapping("/{orderId}")
	public RsData<OrderDetailResDto> getOrderDetail(@PathVariable Long orderId) {
		Order order = orderService.getOrderDetail(orderId);
		return new RsData<>("200", "주문 상세 조회에 성공했습니다.", OrderDetailResDto.from(order));
	}

	@PatchMapping("/{orderId}")
	public RsData<OrderUpdateResDto> updateOrder(
			@PathVariable Long orderId,
			@RequestBody OrderUpdateReqDto request
	) {
		Order order = orderService.updateOrder(orderId, request);
		return new RsData<>("200", "주문 정보가 수정되었습니다.", OrderUpdateResDto.from(order));
	}

	@DeleteMapping("/{orderId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void cancelOrder(@PathVariable Long orderId) {
		orderService.cancelOrder(orderId);
	}

	@PostMapping("/{orderId}/delivery")
	@ResponseStatus(HttpStatus.CREATED)
	public RsData<DeliveryResDto> createDelivery(
		@PathVariable Long orderId,
		@RequestBody @Valid DeliveryReqDto request
	) {
		Delivery delivery = deliveryService.createDelivery(orderId, request);
		return new RsData<>("201", "배송 정보가 생성되었습니다.", DeliveryResDto.from(delivery));
	}

	@GetMapping("/{orderId}/delivery")
	public RsData<DeliveryResDto> getDeliveryByOrder(@PathVariable Long orderId) {
		Delivery delivery = deliveryService.getDeliveryByOrder(orderId);
		return new RsData<>("200", "배송 정보를 조회했습니다.", DeliveryResDto.from(delivery));
	}

	@GetMapping("/{orderId}/payment")
	public RsData<PaymentResDto> getPaymentByOrder(@PathVariable Long orderId) {
		String paymentKey = paymentService.getPaymentKeyByOrder(orderId);
		PaymentResDto dto = tossService.getPaymentInfoByPaymentKey(paymentKey);
		return new RsData<>("200", "결제 정보를 조회했습니다.", dto);
	}
}
