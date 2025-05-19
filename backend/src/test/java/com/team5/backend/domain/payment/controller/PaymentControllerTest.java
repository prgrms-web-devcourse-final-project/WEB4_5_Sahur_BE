package com.team5.backend.domain.payment.controller;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.payment.dto.CancelReqDto;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.security.PrincipalDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class PaymentControllerTest {

    private PaymentController paymentController;
    private PaymentService paymentService;
    private TossService tossService;

    private Long orderId;
    private String paymentKey;
    private int amount;
    private PaymentResDto dto;

    @BeforeEach
    void setup() {
        paymentService = mock(PaymentService.class);
        tossService = mock(TossService.class);
        paymentController = new PaymentController(paymentService, tossService);

        orderId = 2505081357L;
        paymentKey = "tviva20250519145043ABCDE";
        amount = 100000;

        dto = PaymentResDto.builder()
                .paymentKey(paymentKey)
                .orderId(orderId.toString())
                .orderName("테스트")
                .totalAmount(amount)
                .method("카드")
                .status("DONE")
                .approvedAt("2025-05-19")
                .build();
    }

    @Test
    @DisplayName("결제 승인 성공")
    void confirmPayment_success() {
        ConfirmReqDto request = new ConfirmReqDto(paymentKey, orderId, amount);

        RsData<?> result = paymentController.confirmPayment(request);

        verify(tossService).confirmPayment(request);
        verify(paymentService).savePayment(orderId, paymentKey);

        assertEquals(200, result.getStatus());
    }

    @Test
    @DisplayName("결제 내역 단건 조회 - 성공")
    void getPaymentInfo_success() {
        when(paymentService.getPaymentKey(1L)).thenReturn(paymentKey);
        when(tossService.getPaymentInfoByPaymentKey(paymentKey)).thenReturn(dto);

        RsData<PaymentResDto> result = paymentController.getPaymentInfo(1L);

        assertEquals(200, result.getStatus());
        assertEquals(dto, result.getData());
    }

    @Test
    @DisplayName("결제 취소 성공")
    void cancelPayment_success() {
        String cancelReason = "테스트 취소";

        CancelReqDto request = new CancelReqDto(cancelReason);
        when(paymentService.getPaymentKeyByOrder(orderId)).thenReturn(paymentKey);

        RsData<String> result = paymentController.cancelPayment(orderId, request);

        verify(tossService).cancelPayment(paymentKey, cancelReason);
        assertEquals(200, result.getStatus());
        assertEquals(cancelReason, result.getData());
    }

    @Test
    @DisplayName("내 결제 내역 조회 - 성공")
    void getPaymentsByMember_success() {
        OrderStatus status = OrderStatus.PAID;
        String search = "테스트";

        Member member = mock(Member.class);
        when(member.getMemberId()).thenReturn(1L);

        PrincipalDetails user = mock(PrincipalDetails.class);
        when(user.getMember()).thenReturn(member);

        Pageable pageable = PageRequest.of(0, 5);
        Page<String> keys = new PageImpl<>(List.of("key1", "key2"));
        when(paymentService.getPaymentKeysByMember(member.getMemberId(), status, search, pageable)).thenReturn(keys);

        Page<PaymentResDto> mockDtos = new PageImpl<>(List.of(dto, dto));
        when(paymentService.getPaymentsByKeysAsync(keys)).thenReturn(mockDtos);

        RsData<Page<PaymentResDto>> result = paymentController.getPaymentsByMember(user, pageable, status, search);
        assertEquals(200, result.getStatus());
        assertEquals(dto, result.getData().getContent().get(0));
    }

    @Test
    @DisplayName("주문별 결제 조회 - 성공")
    void getPaymentByOrder_success() {
        when(paymentService.getPaymentKeyByOrder(orderId)).thenReturn(paymentKey);
        when(tossService.getPaymentInfoByPaymentKey(paymentKey)).thenReturn(dto);

        RsData<PaymentResDto> result = paymentController.getPaymentByOrder(orderId);

        assertEquals(200, result.getStatus());
        assertEquals(dto, result.getData());
    }
}
