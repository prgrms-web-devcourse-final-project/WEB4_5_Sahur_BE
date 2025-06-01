package com.team5.backend.domain.order.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.groupBuy.service.GroupBuyService;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.notification.redis.NotificationPublisher;
import com.team5.backend.domain.notification.template.NotificationTemplateType;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.dto.OrderPaymentInfoResDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.entity.FilterStatus;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.config.toss.TossPaymentConfig;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.OrderErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final GroupBuyRepository groupBuyRepository;
    private final ProductRepository productRepository;
    private final DeliveryRepository deliveryRepository;

    private final GroupBuyService groupBuyService;

    private final OrderIdGenerator orderIdGenerator;
    private final TossPaymentConfig tossPaymentConfig;
    private final NotificationPublisher notificationPublisher;

    public Order createOrder(OrderCreateReqDto request, Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.MEMBER_NOT_FOUND));

        GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyId())
                .orElseThrow(() -> new CustomException(OrderErrorCode.GROUPBUY_NOT_FOUND));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(OrderErrorCode.PRODUCT_NOT_FOUND));

        Long orderId = orderIdGenerator.generateOrderId();
        Order order = Order.create(orderId, member, groupBuy, product, request.getQuantity());

        // 공동구매 참여 수 증가
        groupBuyService.updateParticipantCount(order.getGroupBuy().getGroupBuyId(), order.getQuantity(), true);

        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Page<OrderListResDto> getOrders(Long orderId, String status, Pageable pageable) {
        Page<Order> orders = null;

        // 주문 ID가 있는 경우 -> 단건 조회
        if (orderId != null) {
            orders = orderRepository.findByOrderId(orderId, pageable);
            return orders.map(OrderListResDto::from);
        }

        // 주문 상태별 필터링
        if (status != null) {
            switch (status.toUpperCase()) {
                case "BEFOREPAID" -> orders = orderRepository.findByStatus(OrderStatus.BEFOREPAID, pageable);
                case "PAID" -> orders = orderRepository.findByStatusAndDelivery_Status(
                        OrderStatus.PAID, DeliveryStatus.PREPARING, pageable
                );
                case "CANCELED" -> orders = orderRepository.findByStatus(OrderStatus.CANCELED, pageable);
                case "INDELIVERY" ->
                        orders = orderRepository.findByDelivery_Status(DeliveryStatus.INDELIVERY, pageable);
                case "COMPLETED" -> orders = orderRepository.findByDelivery_Status(DeliveryStatus.COMPLETED, pageable);
                default -> throw new CustomException(OrderErrorCode.INVALID_ORDER_STATUS);
            }
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(OrderListResDto::from);
    }

    @Transactional(readOnly = true)
    public Page<OrderListResDto> getOrdersByMember(Long memberId, FilterStatus status, Pageable pageable) {
        Page<Order> orders = null;

        if (status == null) {
            status = FilterStatus.ALL; // 기본값 지정
        }

        switch (status) {
            case IN_PROGRESS -> {
                List<OrderStatus> progressStatuses = List.of(OrderStatus.BEFOREPAID, OrderStatus.PAID);
                orders = orderRepository.findByMember_MemberIdAndStatusInOrderByCreatedAtDesc(memberId, progressStatuses, pageable);
            }
            case DONE ->
                    orders = orderRepository.findByDelivery_StatusAndMember_MemberId(DeliveryStatus.COMPLETED, memberId, pageable);
            case CANCELED ->
                    orders = orderRepository.findByMember_MemberIdAndStatusInOrderByCreatedAtDesc(memberId, List.of(OrderStatus.CANCELED), pageable);
            default -> orders = orderRepository.findByMember_MemberId(memberId, pageable);
        }

        return orders.map(OrderListResDto::from);
    }

    @Transactional(readOnly = true)
    public Long getMonthlyCompletedSales() {
        LocalDateTime start = YearMonth.now().atDay(1).atStartOfDay();
        LocalDateTime end = YearMonth.now().atEndOfMonth().atTime(LocalTime.MAX);

        List<Order> paidOrders = orderRepository
                .findAllByStatusAndCreatedAtBetween(OrderStatus.PAID, start, end);

        return paidOrders.stream()
                .mapToLong(Order::getTotalPrice)
                .sum();
    }

    @Transactional(readOnly = true)
    public OrderDetailResDto getOrderDetail(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        return OrderDetailResDto.from(order);
    }

    public Order updateOrder(Long orderId, OrderUpdateReqDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        int newTotalPrice = order.getProduct().getPrice() * request.getQuantity();
        order.updateOrderInfo(request.getQuantity(), newTotalPrice);

        return order;
    }

    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        order.markAsCanceled();

        // 주문 취소 알림 생성
        notificationPublisher.publish(NotificationTemplateType.ORDER_CANCELED, orderId);

        // 공동구매 참여 수 감소
        groupBuyService.updateParticipantCount(order.getGroupBuy().getGroupBuyId(), order.getQuantity(), false);
    }

    public OrderPaymentInfoResDto getOrderPaymentInfo(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

        return OrderPaymentInfoResDto.builder()
                .orderId(order.getOrderId())
                .orderName(order.getProduct().getTitle())
                .amount(order.getTotalPrice())
                .clientKey(tossPaymentConfig.getClientKey())
                .build();
    }

    public void deleteExpiredOrders() {
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<Order> expiredOrders = orderRepository.findByStatusAndCreatedAtBefore(OrderStatus.BEFOREPAID, oneHourAgo);

        for (Order order : expiredOrders) {
            Long orderId = order.getOrderId();
            log.info("[Order Cleanup] 삭제할 주문: orderId={}, createdAt={}", orderId, order.getCreatedAt());

            if (order.getDelivery() != null) {
                log.info("[Order Cleanup] 연결된 배송 정보도 삭제: deliveryId={}", order.getDelivery().getDeliveryId());
                deliveryRepository.delete(order.getDelivery());
            }

            // log.info("[Order Cleanup] 연결된 구매 이력 삭제: orderId={}", orderId);
            // historyRepository.deleteByOrder_OrderId(orderId);

            orderRepository.delete(order);
            log.info("[Order Cleanup] 주문 삭제 완료: orderId={}", orderId);
        }

        log.info("[Order Cleanup] 총 {}건의 주문이 삭제되었습니다.", expiredOrders.size());
    }
}
