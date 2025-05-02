package com.team5.backend.domain.payment.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.team5.backend.domain.payment.dto.CancelReqDto;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.RsData;

public class PaymentControllerTest {

	private PaymentController paymentController;
	private PaymentService paymentService;
	private TossService tossService;

	@BeforeEach
	void setup() {
		paymentService = mock(PaymentService.class);
		tossService = mock(TossService.class);
		paymentController = new PaymentController(paymentService, tossService);
	}

	@Test
	@DisplayName("결제 확인 - 성공")
	void confirmPayment_success() {
		ConfirmReqDto dto = new ConfirmReqDto("key-123", 1L, 1000);
		when(tossService.confirmPayment(dto)).thenReturn(true);

		RsData<?> result = paymentController.confirmPayment(dto);

		verify(tossService).confirmPayment(dto);
		verify(paymentService).savePayment(1L, "key-123");

		assertEquals("200", result.getCode());
		assertEquals("결제에 성공했습니다.", result.getMsg());
	}

	@Test
	@DisplayName("결제 확인 - 실패")
	void confirmPayment_fail() {
		ConfirmReqDto dto = new ConfirmReqDto("key-123", 1L, 1000);
		when(tossService.confirmPayment(dto)).thenReturn(false);

		RsData<?> result = paymentController.confirmPayment(dto);

		verify(tossService).confirmPayment(dto);
		verify(paymentService, never()).savePayment(anyLong(), any());

		assertEquals("400", result.getCode());
		assertEquals("결제에 실패했습니다.", result.getMsg());
	}

	@Test
	@DisplayName("결제 단건 조회 - 성공")
	void getPaymentInfo_success() {
		String paymentKey = "pay-001";
		PaymentResDto dto = PaymentResDto.builder()
			.paymentKey(paymentKey)
			.orderId("order-1")
			.orderName("테스트")
			.totalAmount(5000)
			.method("카드")
			.status("DONE")
			.approvedAt("2025-05-01")
			.build();

		when(paymentService.getPaymentKey(1L)).thenReturn(paymentKey);
		when(tossService.getPaymentInfoByPaymentKey(paymentKey)).thenReturn(dto);

		RsData<?> result = paymentController.getPaymentInfo(1L);

		assertEquals("200", result.getCode());
		assertEquals(dto, result.getData());
	}

	@Test
	@DisplayName("결제 단건 조회 - paymentService에서 예외 발생")
	void getPaymentInfo_fail() {
		when(paymentService.getPaymentKey(999L)).thenThrow(new RuntimeException("결제 정보 없음"));

		assertThrows(RuntimeException.class, () -> paymentController.getPaymentInfo(999L));
	}

	@Test
	@DisplayName("결제 취소 - 성공")
	void cancelPayment_success() {
		CancelReqDto req = new CancelReqDto("테스트 사유");

		when(paymentService.getPaymentKeyByOrder(1L)).thenReturn("pay-123");
		when(tossService.cancelPayment("pay-123", "테스트 사유")).thenReturn(true);

		RsData<?> result = paymentController.cancelPayment(1L, req);

		assertEquals("200", result.getCode());
		assertEquals("결제가 성공적으로 취소되었습니다.", result.getMsg());
	}

	@Test
	@DisplayName("결제 취소 - 실패")
	void cancelPayment_fail() {
		CancelReqDto req = new CancelReqDto("테스트 취소");

		when(paymentService.getPaymentKeyByOrder(1L)).thenReturn("pay-123");
		when(tossService.cancelPayment("pay-123", "테스트 취소")).thenReturn(false);

		RsData<?> result = paymentController.cancelPayment(1L, req);

		assertEquals("400", result.getCode());
		assertEquals("결제 취소에 실패했습니다.", result.getMsg());
	}

	@Test
	@DisplayName("회원 결제 내역 조회 - 성공")
	void getPaymentsByMember_success() {
		PaymentResDto dto = PaymentResDto.builder()
			.paymentKey("pay-key-001")
			.orderId("order-001")
			.orderName("테스트 상품")
			.totalAmount(1000)
			.method("카드")
			.status("DONE")
			.approvedAt("2025-05-01")
			.build();

		when(paymentService.getPaymentKeysByMember(eq(1L), any()))
			.thenReturn(mock(org.springframework.data.domain.Page.class));
		when(paymentService.getPaymentsByKeysAsync(any()))
			.thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(dto)));

		RsData<?> result = paymentController.getPaymentsByMember(1L, null);

		assertEquals("200", result.getCode());
		assertNotNull(result.getData());
	}
}
