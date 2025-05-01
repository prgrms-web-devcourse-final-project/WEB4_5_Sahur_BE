package com.team5.backend.domain.payment.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.global.config.toss.TossPaymentConfig;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;

class TossServiceTest {

	@InjectMocks
	private TossService tossService;

	@Mock
	private RestTemplate restTemplate;

	@Mock
	private TossPaymentConfig tossPaymentConfig;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
		when(tossPaymentConfig.getSecretKey()).thenReturn("test-secret");
	}

	@Test
	@DisplayName("결제 승인 성공")
	void confirmPayment_success() {
		ConfirmReqDto request = new ConfirmReqDto("payKey", 123456L, 1000);

		when(restTemplate.postForEntity(
			anyString(), any(HttpEntity.class), eq(String.class)))
			.thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

		boolean result = tossService.confirmPayment(request);
		System.out.println(">>> 결과: " + result);

		assertTrue(result);
	}

	@Test
	@DisplayName("결제 승인 실패 - 예외 발생")
	void confirmPayment_fail() {
		ConfirmReqDto request = new ConfirmReqDto("payKey", 123456L, 1000);

		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
			.thenThrow(new RestClientException("Toss Error"));

		CustomException e = assertThrows(CustomException.class,
			() -> tossService.confirmPayment(request));
		System.out.println(">>> 에러 내용: " + e);

		assertEquals(PaymentErrorCode.TOSS_CONFIRM_FAILED, e.getErrorCode());
	}

	@Test
	@DisplayName("결제 정보 조회 성공")
	void getPaymentInfo_success() {
		String paymentKey = "pay-123";

		Map<String, Object> card = Map.of(
			"issuerCode", "KB", "acquirerCode", "SH", "number", "1234-5678-****-****"
		);
		Map<String, Object> body = new HashMap<>();
		body.put("paymentKey", paymentKey);
		body.put("orderId", "order-1");
		body.put("orderName", "Test Order");
		body.put("totalAmount", 15000);
		body.put("method", "CARD");
		body.put("status", "DONE");
		body.put("approvedAt", "2025-05-01T12:00:00Z");
		body.put("card", card);

		ResponseEntity<Map<String, Object>> response = new ResponseEntity<>(body, HttpStatus.OK);
		ParameterizedTypeReference<Map<String, Object>> typeRef = new ParameterizedTypeReference<>() {};

		when(restTemplate.exchange(
			anyString(),
			eq(HttpMethod.GET),
			any(HttpEntity.class),
			eq(typeRef))
		).thenReturn(response);

		PaymentResDto result = tossService.getPaymentInfoByPaymentKey(paymentKey);
		System.out.println(">>> 결과: " + result);

		assertEquals("pay-123", result.getPaymentKey());
		assertEquals("Test Order", result.getOrderName());
		assertEquals("KB", result.getIssuerCode());
	}

	@Test
	@DisplayName("결제 정보 조회 실패 - 예외 발생")
	void getPaymentInfo_fail() {
		when(restTemplate.exchange(
			anyString(),
			eq(HttpMethod.GET),
			any(HttpEntity.class),
			any(ParameterizedTypeReference.class))
		).thenThrow(new RestClientException("Toss Error"));

		CustomException e = assertThrows(CustomException.class,
			() -> tossService.getPaymentInfoByPaymentKey("invalid-key"));
		System.out.println(">>> 에러 내용: " + e);

		assertEquals(PaymentErrorCode.TOSS_FETCH_FAILED, e.getErrorCode());
	}

	@Test
	@DisplayName("결제 취소 성공")
	void cancelPayment_success() {
		when(restTemplate.postForEntity(
			anyString(), any(HttpEntity.class), eq(String.class)))
			.thenReturn(new ResponseEntity<>("OK", HttpStatus.OK));

		boolean result = tossService.cancelPayment("payKey", "사용자 요청");
		System.out.println(">>> 결과: " + result);

		assertTrue(result);
	}

	@Test
	@DisplayName("결제 취소 실패 - 예외 발생")
	void cancelPayment_fail() {
		when(restTemplate.postForEntity(anyString(), any(HttpEntity.class), eq(String.class)))
			.thenThrow(new RestClientException("Toss Error"));

		CustomException e = assertThrows(CustomException.class,
			() -> tossService.cancelPayment("payKey", "사유"));
		System.out.println(">>> 에러 내용: " + e);

		assertEquals(PaymentErrorCode.TOSS_CANCEL_FAILED, e.getErrorCode());
	}
}
