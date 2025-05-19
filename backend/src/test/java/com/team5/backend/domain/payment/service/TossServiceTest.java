package com.team5.backend.domain.payment.service;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.AutoConfigureMockRestServiceServer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.util.CardCodeMapper;
import com.team5.backend.global.config.toss.TossPaymentConfig;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@SpringBootTest
@AutoConfigureMockRestServiceServer
class TossServiceTest {

    @Autowired
    private RestClient.Builder restClientBuilder;

    @MockBean
    private TossPaymentConfig tossPaymentConfig;

    @MockBean
    private CardCodeMapper cardCodeMapper;

    private TossService tossService;
    private MockRestServiceServer mockServer;

    @Autowired
    private ObjectMapper objectMapper;

    private ConfirmReqDto confirmReq;

    @BeforeEach
    void setUp() {
        when(tossPaymentConfig.getSecretKey()).thenReturn("testSecretKey");
        when(cardCodeMapper.getInstitutionName(any())).thenReturn("신한카드");

        mockServer = MockRestServiceServer.bindTo(restClientBuilder).build();
        RestClient restClient = restClientBuilder.build();

        this.tossService = new TossService(tossPaymentConfig, restClient, cardCodeMapper);

        confirmReq = new ConfirmReqDto("testKey", 123456789L, 10000);
    }

    @Test
    @DisplayName("결제 승인 성공")
    void confirmPayment_success() throws Exception {
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess());

        tossService.confirmPayment(confirmReq);
    }

    @Test
    @DisplayName("결제 승인 실패")
    void confirmPayment_failure() throws Exception {
        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/confirm"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withServerError());

        assertThatThrownBy(() -> tossService.confirmPayment(confirmReq))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(PaymentErrorCode.TOSS_CONFIRM_FAILED.getMessage());
    }

    @Test
    @DisplayName("결제 단건 조회 성공")
    void getPaymentInfoByPaymentKey_success() throws Exception {
        String paymentKey = "testKey";
        Map<String, Object> responseBody = Map.of(
                "orderId", "123456789",
                "orderName", "Test Order",
                "totalAmount", 10000,
                "method", "카드",
                "status", "DONE",
                "approvedAt", "2024-01-01T00:00:00Z",
                "card", Map.of(
                        "paymentName", "신한카드",
                        "number", "1111-****-****-1111"
                )
        );

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey))
                .andRespond(withSuccess(objectMapper.writeValueAsString(responseBody), MediaType.APPLICATION_JSON));

        PaymentResDto result = tossService.getPaymentInfoByPaymentKey(paymentKey);
        assertThat(result).isNotNull();
        assertThat(result.getOrderId()).isEqualTo("123456789");
        assertThat(result.getMethod()).isEqualTo("카드");
    }

    @Test
    @DisplayName("결제 단건 조회 실패")
    void getPaymentInfoByPaymentKey_failure() throws Exception {
        String paymentKey = "invalidKey";

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey))
                .andRespond(withServerError());

        assertThatThrownBy(() -> tossService.getPaymentInfoByPaymentKey(paymentKey))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(PaymentErrorCode.TOSS_FETCH_FAILED.getMessage());
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_success() {
        String paymentKey = "testKey";
        String cancelReason = "테스트 취소";

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .andExpect(method(org.springframework.http.HttpMethod.POST))
                .andRespond(withSuccess());

        tossService.cancelPayment(paymentKey, cancelReason);
    }

    @Test
    @DisplayName("결제 취소 실패")
    void cancelPayment_failure() {
        String paymentKey = "invalidKey";
        String cancelReason = "잘못된 요청";

        mockServer.expect(requestTo("https://api.tosspayments.com/v1/payments/" + paymentKey + "/cancel"))
                .andRespond(withServerError());

        assertThatThrownBy(() -> tossService.cancelPayment(paymentKey, cancelReason))
                .isInstanceOf(CustomException.class)
                .hasMessageContaining(PaymentErrorCode.TOSS_CANCEL_FAILED.getMessage());
    }
}
