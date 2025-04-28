package com.team5.backend.domain.payment.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team5.backend.domain.payment.entity.Payment;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByOrderMemberId(Long memberId);

	Optional<Payment> findByOrderOrderId(Long orderId);
}
