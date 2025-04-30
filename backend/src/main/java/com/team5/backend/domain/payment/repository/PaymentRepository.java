package com.team5.backend.domain.payment.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team5.backend.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

	@Query(
		value = "SELECT p.paymentKey FROM Payment p WHERE p.order.member.memberId = :memberId",
		countQuery = "SELECT COUNT(p) FROM Payment p WHERE p.order.member.memberId = :memberId"
	)
	Page<String> findPaymentKeysByMemberId(@Param("memberId") Long memberId, Pageable pageable);

	Optional<Payment> findByOrderOrderId(String orderId);
}
