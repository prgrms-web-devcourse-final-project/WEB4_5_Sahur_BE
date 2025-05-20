package com.team5.backend.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    @Query("""
                SELECT p.paymentKey
                FROM Payment p
                JOIN p.order o
                JOIN o.product prod
                WHERE o.member.memberId = :memberId
                  AND (:status IS NULL OR o.status = :status)
                  AND (
                    :search IS NULL OR 
                    STR(o.orderId) LIKE %:search% OR 
                    LOWER(prod.title) LIKE LOWER(CONCAT('%', :search, '%'))
                  )
            """)
    Page<String> findPaymentKeysByMemberId(
            @Param("memberId") Long memberId,
            @Param("status") OrderStatus status,
            @Param("search") String search,
            Pageable pageable
    );

    Optional<Payment> findByOrder_OrderId(Long orderId);
}
