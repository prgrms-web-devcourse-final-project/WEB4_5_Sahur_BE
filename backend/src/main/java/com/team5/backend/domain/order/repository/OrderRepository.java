package com.team5.backend.domain.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findAll(Pageable pageable);

    Page<Order> findByOrderId(Long orderId, Pageable pageable);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Page<Order> findByMember_MemberId(Long memberId, Pageable pageable);

    Page<Order> findByMember_MemberIdAndStatusInOrderByCreatedAtDesc(Long memberId, List<OrderStatus> status, Pageable pageable);

    Page<Order> findByDelivery_StatusAndMember_MemberId(DeliveryStatus status, Long memberId, Pageable pageable);

    List<Order> findAllByStatusAndCreatedAtBetween(OrderStatus status, LocalDateTime start, LocalDateTime end);

    List<Order> findByStatusAndCreatedAtBefore(OrderStatus status, LocalDateTime threshold);
}
