package com.team5.backend.domain.payment.service;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

	private final PaymentRepository paymentRepository;
	private final OrderRepository orderRepository;
	private final TossService tossService;

	public void savePayment(Long orderId, String paymentKey) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(PaymentErrorCode.ORDER_NOT_FOUND));

		// 주문 상태를 PAID 변경
		order.markAsPaid();

		// 결제 엔티티 저장
		Payment payment = Payment.create(order, paymentKey);
		paymentRepository.save(payment);
	}

	public String getPaymentKey(Long paymentId) {
		Payment payment = paymentRepository.findById(paymentId)
			.orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
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
	public String getPaymentKeyByOrder(Long orderId) {
		Payment payment = paymentRepository.findByOrder_OrderId(orderId)
			.orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND_BY_ORDER));
		return payment.getPaymentKey();
	}
}
