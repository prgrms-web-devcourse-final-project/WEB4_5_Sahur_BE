package com.team5.backend.domain.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.*;

import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.global.config.toss.TossPaymentConfig;
import com.team5.backend.global.exception.ServiceException;

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
		} catch (RestClientException e) {
			throw new ServiceException("502-TOSS", "Toss 결제 승인 요청에 실패했습니다.");
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

			// 카드 정보가 포함된 경우 파싱
			Map<String, Object> card = (Map<String, Object>) body.get("card");

			return PaymentResDto.builder()
				.paymentKey((String) body.get("paymentKey"))
				.orderId((String) body.get("orderId"))
				.orderName((String) body.get("orderName"))
				.totalAmount((Integer) body.get("totalAmount"))
				.method((String) body.get("method"))
				.status((String) body.get("status"))
				.approvedAt((String) body.get("approvedAt"))
				.issuerCode(card != null ? (String) card.get("issuerCode") : null)
				.acquirerCode(card != null ? (String) card.get("acquirerCode") : null)
				.cardNumber(card != null ? (String) card.get("number") : null)
				.build();

		} catch (RestClientException e) {
			throw new ServiceException("502-TOSS", "Toss 결제 정보 조회에 실패했습니다.");
		}
	}

	@Async("tossTaskExecutor")
	public CompletableFuture<PaymentResDto> getPaymentInfoAsync(String paymentKey) {
		return CompletableFuture.supplyAsync(() -> getPaymentInfoByPaymentKey(paymentKey));
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
		} catch (RestClientException e) {
			throw new ServiceException("502-TOSS", "Toss 결제 취소 요청에 실패했습니다.");
		}
	}

	// 시크릿 키를 Base64 인코딩
	private String encodeSecretKey(String secretKey) {
		return Base64.getEncoder().encodeToString((secretKey + ":").getBytes(StandardCharsets.UTF_8));
	}
}
