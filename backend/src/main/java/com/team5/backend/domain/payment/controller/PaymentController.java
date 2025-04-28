package com.team5.backend.domain.payment.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.entity.PaymentStatus;
import com.team5.backend.domain.payment.service.PaymentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;

	@PostMapping
	public Payment createPayment(
		@RequestParam Long orderId,
		@RequestParam String paymentKey
	) {
		return paymentService.createPayment(orderId, paymentKey);
	}

	@PostMapping("/confirm")
	public Payment confirmPayment(
		@RequestParam Long paymentId
	) {
		return paymentService.confirmPayment(paymentId);
	}

	@GetMapping("/{paymentId}/status")
	public PaymentStatus getPaymentStatus(@PathVariable Long paymentId) {
		return paymentService.getPaymentStatus(paymentId);
	}

	@GetMapping("/members/{memberId}")
	public List<Payment> getPaymentsByMember(@PathVariable Long memberId) {
		return paymentService.getPaymentsByMember(memberId);
	}

	@GetMapping("/orders/{orderId}")
	public Payment getPaymentByOrder(@PathVariable Long orderId) {
		return paymentService.getPaymentByOrder(orderId);
	}

	@DeleteMapping("/{paymentId}")
	public void cancelPayment(@PathVariable Long paymentId) {
		paymentService.cancelPayment(paymentId);
	}
}
