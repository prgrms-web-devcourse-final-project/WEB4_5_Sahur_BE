package com.team5.backend.domain.payment.controller;

import com.team5.backend.domain.member.member.dto.GetMemberResDto;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.payment.dto.CancelReqDto;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

public class PaymentControllerTest {

	private PaymentController paymentController;
	private PaymentService paymentService;
	private TossService tossService;
	private AuthService authService;

	@BeforeEach
	void setup() {
		paymentService = mock(PaymentService.class);
		tossService = mock(TossService.class);
		authService = mock(AuthService.class);
		paymentController = new PaymentController(paymentService, tossService, authService);
	}

	@Test
	@DisplayName("결제 승인 - 성공")
	void confirmPayment_success() {
		ConfirmReqDto dto = new ConfirmReqDto("key-123", 1L, 1000);
		when(tossService.confirmPayment(dto)).thenReturn(true);

		RsData<Empty> result = paymentController.confirmPayment(dto);

		verify(tossService).confirmPayment(dto);
		verify(paymentService).savePayment(1L, "key-123");

		assertEquals("200-0", result.getMsg());
		assertEquals("결제 승인 성공", result.getMsg());
	}

	@Test
	@DisplayName("결제 내역 단건 조회 - 성공")
	void getPaymentInfo_success() {
		String paymentKey = "pay-001";
		PaymentResDto dto = PaymentResDto.builder()
			.paymentKey(paymentKey)
			.orderId("ODR-2505081234")
			.orderName("테스트")
			.totalAmount(5000)
			.method("카드")
			.status("DONE")
			.approvedAt("2025-05-08")
			.build();

		when(paymentService.getPaymentKey(1L)).thenReturn(paymentKey);
		when(tossService.getPaymentInfoByPaymentKey(paymentKey)).thenReturn(dto);

		RsData<PaymentResDto> result = paymentController.getPaymentInfo(1L);

		assertEquals("200-0", result.getMsg());
		assertEquals("결제 내역 조회 성공", result.getMsg());
		assertEquals(dto, result.getData());
	}

	@Test
	@DisplayName("결제 취소 - 성공")
	void cancelPayment_success() {
		CancelReqDto req = new CancelReqDto("테스트 사유");

		when(paymentService.getPaymentKeyByOrder(1L)).thenReturn("pay-123");
		when(tossService.cancelPayment("pay-123", "테스트 사유")).thenReturn(true);

		RsData<String> result = paymentController.cancelPayment(1L, req);

		assertEquals("200-0", result.getMsg());
		assertEquals("결제 취소 성공", result.getMsg());
		assertEquals("테스트 사유", result.getData());
	}

	@Test
	@DisplayName("내 결제 내역 조회 - 성공")
	void getPaymentsByMember_success() {
		PaymentResDto dto = PaymentResDto.builder()
			.paymentKey("pay-key-001")
			.orderId("ORD-2505085678")
			.orderName("테스트 상품")
			.totalAmount(1000)
			.method("카드")
			.status("DONE")
			.approvedAt("2025-05-01")
			.build();

		when(authService.getLoggedInMember(anyString()))
				.thenReturn(mock(GetMemberResDto.class));
		when(paymentService.getPaymentKeysByMember(eq(1L), any()))
			.thenReturn(mock(org.springframework.data.domain.Page.class));
		when(paymentService.getPaymentsByKeysAsync(any()))
			.thenReturn(new org.springframework.data.domain.PageImpl<>(List.of(dto)));

		RsData<Page<PaymentResDto>> result = paymentController.getPaymentsByMember("token", null);

		assertEquals("200-0", result.getMsg());
		assertEquals("전체 결제 내역 비동기 조회 성공", result.getMsg());
		assertNotNull(result.getData());
	}

	@Test
	@DisplayName("주문별 결제 조회 - 성공")
	void getPaymentByOrder_success() {
		String paymentKey = "pay-order-001";

		PaymentResDto dto = PaymentResDto.builder()
				.paymentKey(paymentKey)
				.orderId("ORD-2505081357")
				.orderName("주문결제테스트")
				.totalAmount(3000)
				.method("카드")
				.status("DONE")
				.approvedAt("2025-05-02")
				.build();

		when(paymentService.getPaymentKeyByOrder(888L)).thenReturn(paymentKey);
		when(tossService.getPaymentInfoByPaymentKey(paymentKey)).thenReturn(dto);

		RsData<PaymentResDto> result = paymentController.getPaymentByOrder(888L);

		assertEquals("200-0", result.getMsg());
		assertEquals("주문별 결제 내역 조회 성공", result.getMsg());
		assertEquals(dto, result.getData());
	}
}
