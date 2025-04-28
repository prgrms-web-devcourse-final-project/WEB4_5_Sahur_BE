package com.team5.backend.domain.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.entity.PaymentStatus;
import com.team5.backend.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;

	public Payment createPayment(Long orderId, String paymentKey) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		Payment payment = Payment.create(order, paymentKey);
		return paymentRepository.save(payment);
	}

	@Transactional(readOnly = true)
	public List<Payment> getPaymentsByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		return paymentRepository.findByOrderMemberId(memberId);
	}

	@Transactional(readOnly = true)
	public Payment getPaymentByOrder(Long orderId) {
		return paymentRepository.findByOrderOrderId(orderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제가 존재하지 않습니다."));
	}

	public void cancelPayment(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

		// 토스 페이먼츠 호출해서 결제 취소 로직 진행
	}
}
