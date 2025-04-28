package com.team5.backend.domain.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.team5.backend.domain.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
