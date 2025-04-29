package com.team5.backend.domain.payment.repository;

import com.team5.backend.domain.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
	List<Payment> findByOrderMemberMemberId(Long memberId);

	Optional<Payment> findByOrderOrderId(Long orderId);
}
