package com.team5.backend.domain.payment.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.payment.dto.CancelReqDto;
import com.team5.backend.domain.payment.dto.ConfirmReqDto;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.service.PaymentService;
import com.team5.backend.domain.payment.service.TossService;
import com.team5.backend.global.dto.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

	private final PaymentService paymentService;
	private final TossService tossService;

	@PostMapping("/confirm")
	public RsData<?> confirmPayment(@RequestBody @Valid ConfirmReqDto request) {
		boolean isConfirm = tossService.confirmPayment(request);

		if (isConfirm) {
			paymentService.savePayment(request.getOrderId(), request.getPaymentKey());
			return new RsData<>("200", "결제에 성공했습니다.");
		} else {
			return new RsData<>("400-1", "결제에 실패했습니다.");
		}
	}

	@GetMapping("/{paymentId}")
	public RsData<PaymentResDto> getPaymentInfo(@PathVariable("paymentId") String paymentId) {
		try {
			String paymentKey = paymentService.getPaymentKey(paymentId);
			PaymentResDto dto = tossService.getPaymentInfoByPaymentKey(paymentKey);
			return new RsData<>("200", "결제 정보를 불러왔습니다.", dto);
		} catch (RuntimeException e) {
			return new RsData<>("404-1", "결제 정보를 찾을 수 없습니다.");
		}
	}

	@PostMapping("/{orderId}/cancel")
	public RsData<?> cancelPayment(
		@PathVariable("orderId") String orderId,
		@RequestBody @Valid CancelReqDto request
	) {
		try {
			String paymentKey = paymentService.getPaymentKeyByOrder(orderId);
			boolean isCanceled = tossService.cancelPayment(paymentKey, request.getCancelReason());

			if (isCanceled) {
				return new RsData<>("200", "결제가 성공적으로 취소되었습니다.");
			} else {
				return new RsData<>("400-2", "결제 취소에 실패했습니다.");
			}
		} catch (IllegalArgumentException e) {
			return new RsData<>("404-1", "결제 정보를 찾을 수 없습니다.");
		}
	}

	@GetMapping("/members/{memberId}")
	public RsData<Page<PaymentResDto>> getPaymentsByMember(
		@PathVariable Long memberId,
		@PageableDefault(size = 5, sort = "paymentId", direction = Sort.Direction.DESC) Pageable pageable
		) {
		Page<String> paymentKeys = paymentService.getPaymentKeysByMember(memberId, pageable);
		Page<PaymentResDto> result = paymentService.getPaymentsByKeysAsync(paymentKeys);

		return new RsData<>("200", "결제 내역을 비동기로 조회했습니다.", result);
	}
}
