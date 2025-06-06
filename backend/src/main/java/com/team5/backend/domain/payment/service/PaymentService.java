package com.team5.backend.domain.payment.service;

import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.notification.redis.NotificationPublisher;
import com.team5.backend.domain.notification.repository.NotificationRepository;
import com.team5.backend.domain.notification.template.NotificationTemplateType;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.order.service.OrderQueryService;
import com.team5.backend.domain.payment.dto.PaymentResDto;
import com.team5.backend.domain.payment.entity.Payment;
import com.team5.backend.domain.payment.repository.PaymentRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.PaymentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

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
    private final OrderQueryService orderQueryService;
    private final NotificationPublisher notificationPublisher;

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

        // 구매 완료 알림 생성
        notificationPublisher.publish(NotificationTemplateType.PURCHASED, orderId);
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
    public Page<String> getPaymentKeysByMember(Long memberId, OrderStatus status, String search, Pageable pageable) {
        return paymentRepository.findPaymentKeysByMemberId(memberId, status, search, pageable);
    }

    @Transactional(readOnly = true)
    public String getPaymentKeyByOrder(Long orderId) {
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new CustomException(PaymentErrorCode.PAYMENT_NOT_FOUND_BY_ORDER));
        return payment.getPaymentKey();
    }

    @Transactional
    public void cancelPaymentsByGroupBuyId(Long groupBuyId, String reason) {
        List<Order> orders = orderQueryService.getOrdersByGroupBuyId(groupBuyId);
        log.info("조회된 주문 수: {}", orders.size());

        for (Order order : orders) {
            paymentRepository.findByOrder_OrderId(order.getOrderId())
                    .ifPresent(payment -> {
                            tossService.cancelPayment(payment.getPaymentKey(), reason);
                    });
        }
    }
}
