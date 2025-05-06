package com.team5.backend.domain.delivery.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team5.backend.domain.delivery.entity.Delivery;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
	Optional<Delivery> findByOrderOrderId(Long orderId);
}
