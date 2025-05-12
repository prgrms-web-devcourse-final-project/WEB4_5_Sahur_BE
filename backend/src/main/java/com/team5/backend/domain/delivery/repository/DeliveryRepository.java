package com.team5.backend.domain.delivery.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.order.entity.Order;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
    Optional<Delivery> findByOrder_OrderId(Long orderId);

    @Query("SELECT o FROM Delivery d JOIN d.order o WHERE d.status = :status AND o.member.memberId = :memberId ORDER BY o.createdAt DESC")
    Page<Order> findOrdersByDeliveryStatusAndMemberId(@Param("status") DeliveryStatus status, @Param("memberId") Long memberId, Pageable pageable);

    long countByStatus(DeliveryStatus status);
}
