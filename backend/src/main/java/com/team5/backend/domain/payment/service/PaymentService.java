package com.team5.backend.domain.payment.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.notification.entity.Notification;
import com.team5.backend.domain.notification.entity.NotificationType;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;
    private final TossService tossService;
    private final HistoryRepository historyRepository;
    private final NotificationRepository notificationRepository;

    @Transactional
    public void savePayment(Long orderId, String paymentKey) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.ORDER_NOT_FOUND));

        // 주문 상태를 PAID 변경
        // 주문 상태를 PAID로 변경
        order.markAsPaid();

        // 결제 엔티티 저장
        Payment payment = Payment.create(order, paymentKey);
        paymentRepository.save(payment);

        // History 생성
        History history = History.builder()
                .member(order.getMember())
                .product(order.getProduct())
                .groupBuy(order.getGroupBuy())
                .order(order)
                .writable(true)
                .build();
        historyRepository.save(history);

        // Notification 생성
        Notification notification = Notification.builder()
                .member(order.getMember())
                .type(NotificationType.ORDER)
                .title("구매 완료 알림")
                .message("[" + order.getProduct().getTitle() + "] 상품의 구매가 완료되었습니다.")
                .url("/orders/" + order.getOrderId()) // 예시 URL
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    public String getPaymentKey(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND));
        return payment.getPaymentKey();
    }

    public Page<PaymentResDto> getPaymentsByKeysAsync(Page<String> paymentKeys) {
        List<CompletableFuture<PaymentResDto>> futures = paymentKeys.stream()
                .map(key -> tossService.getPaymentInfoAsync(key)
                        .exceptionally(e -> {
                            log.error("[비동기 Toss 결제 조회 실패] paymentKey: {}", key, e);
                            return null;
                        }))
                .toList();

        List<PaymentResDto> dtoList = futures.stream()
                .map(CompletableFuture::join)
                .filter(dto -> dto != null)
                .toList();

        return new PageImpl<>(dtoList, paymentKeys.getPageable(), paymentKeys.getTotalElements());
    }

    @Transactional(readOnly = true)
    public Page<String> getPaymentKeysByMember(Long memberId, Pageable pageable) {
        return paymentRepository.findPaymentKeysByMemberId(memberId, pageable);
    }

    @Transactional(readOnly = true)
    public String getPaymentKeyByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND_BY_ORDER));
        return payment.getPaymentKey();
    }
}
