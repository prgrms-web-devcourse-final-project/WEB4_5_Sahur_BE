package com.team5.backend.domain.payment.service;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PaymentServiceTest {

	@InjectMocks
	private PaymentService paymentService;

	@Mock
	private PaymentRepository paymentRepository;

	@Mock
	private OrderRepository orderRepository;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("결제 저장 성공 - 주문 존재할 경우")
	void savePayment_success() {
		Long orderId = 1L;
		String paymentKey = "pay-123";
		Order order = mock(Order.class);
		when(orderRepository.findById(orderId)).thenReturn(Optional.of(order));
		when(paymentRepository.save(any(Payment.class))).thenReturn(null);

		paymentService.savePayment(orderId, paymentKey);

		verify(order).markAsPaid();
		verify(paymentRepository).save(any(Payment.class));
	}

	@Test
	@DisplayName("결제 저장 실패 - 주문 존재하지 않음")
	void savePayment_fail_orderNotFound() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> paymentService.savePayment(99L, "abc"));

		assertEquals(PaymentErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("결제 키 조회 성공 - 결제 존재할 경우")
	void getPaymentKey_success() {
		Long paymentId = 10L;
		Payment payment = mock(Payment.class);
		when(payment.getPaymentKey()).thenReturn("key-123");
		when(paymentRepository.findById(paymentId)).thenReturn(Optional.of(payment));

		String result = paymentService.getPaymentKey(paymentId);

		assertEquals("key-123", result);
	}

	@Test
	@DisplayName("결제 키 조회 실패 - 결제 없음")
	void getPaymentKey_fail_notFound() {
		when(paymentRepository.findById(anyLong())).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> paymentService.getPaymentKey(100L));

		assertEquals(PaymentErrorCode.PAYMENT_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 ID로 결제 키 조회 성공")
	void getPaymentKeyByOrder_success() {
		Long orderId = 3L;
		Payment payment = mock(Payment.class);
		when(payment.getPaymentKey()).thenReturn("order-key");
		when(paymentRepository.findByOrder_OrderId(orderId)).thenReturn(Optional.of(payment));

		String result = paymentService.getPaymentKeyByOrder(orderId);

		assertEquals("order-key", result);
	}

	@Test
	@DisplayName("주문 ID로 결제 키 조회 실패 - 결제 없음")
	void getPaymentKeyByOrder_fail_notFound() {
		when(paymentRepository.findByOrder_OrderId(anyLong())).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> paymentService.getPaymentKeyByOrder(123L));

		assertEquals(PaymentErrorCode.PAYMENT_NOT_FOUND_BY_ORDER, e.getErrorCode());
	}
}
