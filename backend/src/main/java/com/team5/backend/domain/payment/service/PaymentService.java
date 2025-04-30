package com.team5.backend.domain.payment.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.dto.PaymentResDto;
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
	private final TossService tossService;

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

	public Page<PaymentResDto> getPaymentsByKeysAsync(Page<String> paymentKeys) {
		List<CompletableFuture<PaymentResDto>> futures = paymentKeys
			.stream()
			.map(tossService::getPaymentInfoAsync)
			.toList();

		CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

		List<PaymentResDto> dtoList = futures.stream()
			.map(CompletableFuture::join)
			.toList();

		return new PageImpl<>(dtoList, paymentKeys.getPageable(), paymentKeys.getTotalElements());
	}

	@Transactional(readOnly = true)
	public Page<String> getPaymentKeysByMember(Long memberId, Pageable pageable) {
		return paymentRepository.findPaymentKeysByMemberId(memberId, pageable);
	}

	@Transactional(readOnly = true)
	public String getPaymentKeyByOrder(String orderId) {
		Payment payment = paymentRepository.findByOrderOrderId(orderId)
			.orElseThrow(() -> new IllegalArgumentException("해당 주문의 결제가 존재하지 않습니다."));
		return payment.getPaymentKey();
	}
}
