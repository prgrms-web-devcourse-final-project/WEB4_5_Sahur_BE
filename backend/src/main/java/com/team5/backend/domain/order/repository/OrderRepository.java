package com.team5.backend.domain.order.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;

public interface OrderRepository extends JpaRepository<Order, Long> {
	@EntityGraph(attributePaths = {"member", "groupBuy", "product"})
	Page<Order> findAll(Pageable pageable);

	Page<Order> findByOrderId(Long orderId, Pageable pageable);

	Page<Order> findByStatus(OrderStatus status, Pageable pageable);

	@EntityGraph(attributePaths = {"member", "groupBuy", "product"})
	Optional<Order> findWithDetailsByOrderId(Long orderId);
}
