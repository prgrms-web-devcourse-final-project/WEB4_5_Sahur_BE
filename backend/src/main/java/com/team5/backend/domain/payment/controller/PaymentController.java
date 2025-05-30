package com.team5.backend.domain.payment.controller;

import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.payment.dto.CancelReqDto;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Payment", description = "결제 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final TossService tossService;

    @Operation(summary = "결제 승인", description = "해당 주문에 대한 결제 승인 및 paymentKey 저장")
    @PostMapping("/confirm")
    public RsData<Empty> confirmPayment(
            @RequestBody @Valid ConfirmReqDto request
    ) {
        tossService.confirmPayment(request);
        paymentService.savePayment(request.getOrderId(), request.getPaymentKey());
        return RsDataUtil.success("결제 승인 성공");
    }

    @Operation(summary = "결제 내역 단건 조회", description = "결제 ID에 대한 결제 내역 단건 조회")
    @GetMapping("/{paymentId}")
    public RsData<PaymentResDto> getPaymentInfo(@PathVariable("paymentId") Long paymentId) {
        String paymentKey = paymentService.getPaymentKey(paymentId);
        PaymentResDto dto = tossService.getPaymentInfoByPaymentKey(paymentKey);
        return RsDataUtil.success("결제 내역 조회 성공", dto);
    }

    @Operation(summary = "전체 결제 취소", description = "해당 공동구매 관련 전체 결제 취소")
    @PostMapping("/groupBuy/{groupBuyId}/cancel")
    public RsData<String> cancelPaymentsByGroupBuyId(
            @Parameter(description = "공동구매 ID") @PathVariable("groupBuyId") Long groupBuyId) {
            paymentService.cancelPaymentsByGroupBuyId(groupBuyId, "공동구매 관련 결제 일괄 취소");
            return RsDataUtil.success("결제 취소 성공", "공동구매 관련 결제 일괄 취소가 완료되었습니다.");
    }

    @Operation(summary = "결제 취소", description = "해당 주문에 대한 결제 취소")
    @PostMapping("/order/{orderId}/cancel")
    public RsData<String> cancelPayment(
            @Parameter(description = "주문 ID") @PathVariable("orderId") Long orderId,
            @RequestBody @Valid CancelReqDto request
    ) {
        String paymentKey = paymentService.getPaymentKeyByOrder(orderId);
        tossService.cancelPayment(paymentKey, request.getCancelReason());
        return RsDataUtil.success("결제 취소 성공", request.getCancelReason());
    }

    @Operation(summary = "내 결제 조회", description = "로그인한 회원의 결제 내역 전체 조회")
    @GetMapping("/me")
    public RsData<Page<PaymentResDto>> getPaymentsByMember(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @ParameterObject
            @PageableDefault(size = 5, sort = "paymentId", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(name = "status", required = false) OrderStatus status,
            @RequestParam(name = "search", required = false) String search
    ) {
        Long memberId = userDetails.getMember().getMemberId();

        Page<String> paymentKeys = paymentService.getPaymentKeysByMember(memberId, status, search, pageable);
        Page<PaymentResDto> result = paymentService.getPaymentsByKeysAsync(paymentKeys);

        return RsDataUtil.success("전체 결제 내역 비동기 조회 성공", result);
    }

    @Operation(summary = "주문별 결제 조회", description = "주문 ID에 대한 결제 내역 단건 조회")
    @GetMapping("/order/{orderId}")
    public RsData<PaymentResDto> getPaymentByOrder(
            @Parameter(description = "주문 ID") @PathVariable Long orderId
    ) {
        String paymentKey = paymentService.getPaymentKeyByOrder(orderId);
        PaymentResDto dto = tossService.getPaymentInfoByPaymentKey(paymentKey);
        return RsDataUtil.success("주문별 결제 내역 조회 성공", dto);
    }
}
