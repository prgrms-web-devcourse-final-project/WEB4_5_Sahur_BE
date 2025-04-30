package com.team5.backend.domain.payment.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;

	public void savePayment(String orderId, String paymentKey) {
		Order order = orderRepository.findById(Long.valueOf(orderId))
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		Payment payment = new Payment(order, paymentKey);
		paymentRepository.save(payment);
	}

	public String getPaymentKey(String paymentId) {
		Payment payment = paymentRepository.findById(Long.valueOf(paymentId))
			.orElseThrow(() -> new IllegalArgumentException("결제를 찾을 수 없습니다."));

		return payment.getPaymentKey();
	}

	@Transactional(readOnly = true)
	public List<Payment> getPaymentsByMember(Long memberId) {
		Member member = memberRepository.findById(memberId)
			.orElseThrow(() -> new IllegalArgumentException("회원이 존재하지 않습니다."));
		return paymentRepository.findByOrderMemberMemberId(memberId);
	}

	@Transactional(readOnly = true)
	public String getPaymentByOrder(String orderId) {
		Payment payment = paymentRepository.findByOrderOrderId(orderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제가 존재하지 않습니다."));
		return payment.getPaymentKey();
	}
}
