package com.team5.backend.domain.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.global.config.TossPaymentConfig;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TossService {

	private final TossPaymentConfig tossPaymentConfig;
	private final RestTemplate restTemplate;

	/**
	 * 결제 승인 요청 (Toss /payments/confirm)
	 */
	public boolean confirmPayment(ConfirmReqDto request) {
		String url = "https://api.tosspayments.com/v1/payments/confirm";

		// HTTP 헤더 설정
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encodeSecretKey(tossPaymentConfig.getSecretKey()));

		// 요청 본문 설정
		Map<String, Object> body = new HashMap<>();
		body.put("paymentKey", request.getPaymentKey());
		body.put("orderId", request.getOrderId());
		body.put("amount", request.getAmount());

		// HTTP 요청 엔티티 구성
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(body, headers);

		// Toss 서버로 POST 요청
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
			// Toss 서버 관련 예외 처리
			System.err.println("Toss 결제 실패: " + e.getMessage());
			return false;
		}
	}

	/**
	 * paymentKey 이용해 결제 상세 정보 조회 (Toss /payments/{paymentKey})
	 */
	public PaymentResDto getPaymentInfoByPaymentKey(String paymentKey) {
		String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;

		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", "Basic " + encodeSecretKey(tossPaymentConfig.getSecretKey()));
		HttpEntity<Void> entity = new HttpEntity<>(headers);

		try {
			ResponseEntity<Map<String, Object>> response = restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<>() {});
			Map<String, Object> body = response.getBody();

			if (body == null) {
				throw new RuntimeException("응답 body가 null 입니다.");
			}

			return PaymentResDto.builder()
				.paymentKey((String) body.get("paymentKey"))
				.orderId((String) body.get("orderId"))
				.orderName((String) body.get("orderName"))
				.totalAmount((Integer) body.get("totalAmount"))
				.method((String) body.get("method"))
				.status((String) body.get("status"))
				.approvedAt((String) body.get("approvedAt"))
				.build();
		} catch (Exception e) {
			throw new RuntimeException("결제 조회 실패: " + e.getMessage());
		}
	}

	/**
	 * 결제 취소 요청 (Toss /payments/{paymentKey}/cancel)
	 */
	public boolean cancelPayment(String paymentKey, String cancelReason) {
		String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set("Authorization", "Basic " + encodeSecretKey(tossPaymentConfig.getSecretKey()));

		Map<String, Object> body = new HashMap<>();
		body.put("cancelReason", cancelReason);

		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
			System.err.println("Toss 결제 실패: " + e.getMessage());
			return false;
		}
	}

	// 시크릿 키를 Base64 인코딩
	private String encodeSecretKey(String secretKey) {
		return Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	}
}
