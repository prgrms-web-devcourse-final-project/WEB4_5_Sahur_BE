package com.team5.backend.domain.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.team5.backend.domain.payment.dto.ConfirmReqDto;
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
		Map<String, Object> requestBody = new HashMap<>();
		requestBody.put("paymentKey", request.getPaymentKey());
		requestBody.put("orderId", request.getOrderId());
		requestBody.put("amount", request.getAmount());

		// HTTP 요청 엔티티 구성
		HttpEntity<Map<String, Object>> httpEntity = new HttpEntity<>(requestBody, headers);

		// Toss 서버로 POST 요청
		try {
			ResponseEntity<String> response = restTemplate.postForEntity(url, httpEntity, String.class);
			return response.getStatusCode().is2xxSuccessful();
		} catch (HttpClientErrorException | HttpServerErrorException | ResourceAccessException e) {
			// Toss 서버 관련 예외 처리
			return false;
		}
	}

	// 시크릿 키를 Base64 인코딩
	private String encodeSecretKey(String secretKey) {
		return Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	}
}
