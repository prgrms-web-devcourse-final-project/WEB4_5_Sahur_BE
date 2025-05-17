package com.team5.backend.domain.payment.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.payment.dto.CancelReqDto;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.security.PrincipalDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
    @DisplayName("결제 승인 성공")
    void confirmPayment_success() {
        ConfirmReqDto request = new ConfirmReqDto("payKey", 123456L, 10000);

        RsData<?> result = paymentController.confirmPayment(request);

        verify(tossService).confirmPayment(request);
        verify(paymentService).savePayment(123456L, "payKey");

        assertEquals(200, result.getStatus());
    }

    @Test
    @DisplayName("결제 내역 단건 조회 - 성공")
    void getPaymentInfo_success() {
        String paymentKey = "pay-001";
        PaymentResDto dto = PaymentResDto.builder()
                .paymentKey(paymentKey)
                .orderId("2505081234")
                .orderName("테스트")
                .totalAmount(5000)
                .method("카드")
                .status("DONE")
                .approvedAt("2025-05-08")
                .build();

        when(paymentService.getPaymentKey(1L)).thenReturn(paymentKey);
        when(tossService.getPaymentInfoByPaymentKey(paymentKey)).thenReturn(dto);

        RsData<PaymentResDto> result = paymentController.getPaymentInfo(1L);

        assertEquals(200, result.getStatus());
        assertEquals(dto, result.getData());
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_success() {
        CancelReqDto request = new CancelReqDto("테스트 취소");
        when(paymentService.getPaymentKeyByOrder(123L)).thenReturn("payKey");

        RsData<String> result = paymentController.cancelPayment(123L, request);

        verify(tossService).cancelPayment("payKey", "테스트 취소");
        assertEquals(200, result.getStatus());
        assertEquals("테스트 취소", result.getData());
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

        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);

        PrincipalDetails user = mock(PrincipalDetails.class);
        when(user.getMember()).thenReturn(member);

        Page<String> mockKeys = new PageImpl<>(List.of("key1", "key2"));
        Page<PaymentResDto> mockDtos = new PageImpl<>(List.of(dto, dto));

        when(paymentService.getPaymentKeysByMember(eq(1L), any())).thenReturn(mockKeys);
        when(paymentService.getPaymentsByKeysAsync(mockKeys)).thenReturn(mockDtos);

        RsData<Page<PaymentResDto>> result = paymentController.getPaymentsByMember(user, PageRequest.of(0, 5));

        assertEquals(200, result.getStatus());
        assertEquals(2, result.getData().getContent().size());
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

        assertEquals(200, result.getStatus());
        assertEquals(dto, result.getData());
    }
}
