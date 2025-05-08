package com.team5.backend.domain.order.repository;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
	Page<Order> findAll(Pageable pageable);

	Page<Order> findByOrderId(Long orderId, Pageable pageable);

	Page<Order> findByStatus(OrderStatus status, Pageable pageable);

	Page<Order> findByMember_MemberId(Long memberId, Pageable pageable);

	Page<Order> findByMember_MemberIdAndStatusIn(Long memberId, List<OrderStatus> status, Pageable pageable);
}
