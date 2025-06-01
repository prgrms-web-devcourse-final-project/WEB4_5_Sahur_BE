package com.team5.backend.domain.order.service;

import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderQueryService {

    private final OrderRepository orderRepository;

    public List<Order> getOrdersByGroupBuyId(Long groupBuyId) {
        return orderRepository.findAllByGroupBuy_GroupBuyId(groupBuyId);
    }
}