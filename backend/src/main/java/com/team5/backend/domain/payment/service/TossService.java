package com.team5.backend.domain.payment.service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.util.CardCodeMapper;
import com.team5.backend.global.config.toss.TossPaymentConfig;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class TossService {

    private final TossPaymentConfig tossPaymentConfig;
    private final RestClient restClient;
    private final CardCodeMapper cardCodeMapper;

    private String encodeSecretKey() {
        return "Basic " + Base64.getEncoder()
                .encodeToString((tossPaymentConfig.getSecretKey() + ":").getBytes(StandardCharsets.UTF_8));
    }

    /**
     * 결제 승인 요청 (Toss /payments/confirm)
     */
    public void confirmPayment(ConfirmReqDto request) {
        String url = "https://api.tosspayments.com/v1/payments/confirm";

        // 요청 본문 설정
        Map<String, Object> body = Map.of(
                "paymentKey", request.getPaymentKey(),
                "orderId", request.getOrderId(),
                "amount", request.getAmount()
        );

        // Toss 서버로 POST 요청
        try {
            restClient.post()
                    .uri(url)
                    .headers(h -> {
                        h.setContentType(MediaType.APPLICATION_JSON);
                        h.set("Authorization", encodeSecretKey());
                    })
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("[Toss 결제 승인 성공] orderId: {}, paymentKey: {}", request.getOrderId(), request.getPaymentKey());
        } catch (RestClientException e) {
            log.error("[Toss 결제 승인 실패] orderId: {}, paymentKey: {}", request.getOrderId(), request.getPaymentKey(), e);
            throw new CustomException(PaymentErrorCode.TOSS_CONFIRM_FAILED);
        }
    }

    /**
     * paymentKey 이용해 결제 상세 정보 조회 (Toss /payments/{paymentKey})
     */
    public PaymentResDto getPaymentInfoByPaymentKey(String paymentKey) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey;

        try {
            Map<String, Object> body = restClient.get()
                    .uri(url)
                    .headers(h -> h.set("Authorization", encodeSecretKey()))
                    .retrieve()
                    .body(new ParameterizedTypeReference<>() {
                    });

            if (body == null) {
                log.error("[Toss 결제 조회 실패] paymentKey: {}, 응답 본문이 null", paymentKey);
                throw new CustomException(PaymentErrorCode.TOSS_FETCH_FAILED);
            }

            log.info("[Toss 결제 조회 성공] paymentKey: {}", paymentKey);

            String method = (String) body.get("method");
            String paymentName = null;
            String cardNumber = null;

            switch (method) {
                case "카드" -> {
                    Map<String, Object> card = (Map<String, Object>) body.get("card");
                    String issuerCode = (String) card.get("issuerCode");
                    paymentName = cardCodeMapper.getInstitutionName(issuerCode);
                    cardNumber = (String) card.get("number");
                }
                case "간편결제" -> {
                    Map<String, Object> easyPay = (Map<String, Object>) body.get("easyPay");
                    paymentName = (String) easyPay.get("provider");
                }
            }

            return PaymentResDto.builder()
                    .paymentKey((String) body.get("paymentKey"))
                    .orderId((String) body.get("orderId"))
                    .orderName((String) body.get("orderName"))
                    .totalAmount((Integer) body.get("totalAmount"))
                    .method(method)
                    .status((String) body.get("status"))
                    .approvedAt((String) body.get("approvedAt"))
                    .paymentName(paymentName)
                    .cardNumber(cardNumber)
                    .build();

        } catch (RestClientException e) {
            log.error("[Toss 결제 조회 실패] paymentKey: {}", paymentKey, e);
            throw new CustomException(PaymentErrorCode.TOSS_FETCH_FAILED);
        }
    }

    @Async("tossTaskExecutor")
    public CompletableFuture<PaymentResDto> getPaymentInfoAsync(String paymentKey) {
        return CompletableFuture.supplyAsync(() -> getPaymentInfoByPaymentKey(paymentKey));
    }

    /**
     * 결제 취소 요청 (Toss /payments/{paymentKey}/cancel)
     */
    public void cancelPayment(String paymentKey, String cancelReason) {
        String url = "https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel";

        Map<String, Object> body = Map.of("cancelReason", cancelReason);

        try {
            restClient.post()
                    .uri(url)
                    .headers(h -> {
                        h.setContentType(MediaType.APPLICATION_JSON);
                        h.set("Authorization", encodeSecretKey());
                    })
                    .body(body)
                    .retrieve()
                    .toBodilessEntity();

            log.info("[Toss 결제 취소 성공] paymentKey: {}, 사유: {}", paymentKey, cancelReason);
        } catch (RestClientException e) {
            log.error("[Toss 결제 취소 실패] paymentKey: {}", paymentKey, e);
            throw new CustomException(PaymentErrorCode.TOSS_CANCEL_FAILED);
        }
    }

}
