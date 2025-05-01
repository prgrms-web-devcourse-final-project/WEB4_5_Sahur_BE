package com.team5.backend.domain.order.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.order.dto.*;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final GroupBuyRepository groupBuyRepository;

	public Order createOrder(OrderCreateReqDto request) {
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

		GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyId())
				.orElseThrow(() -> new IllegalArgumentException("존재하지 않는 공동구매입니다."));

		Order order = Order.create(member, groupBuy, request.getQuantity());

		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public Page<OrderListResDto> getOrders(Pageable pageable) {
		return orderRepository.findAll(pageable)
			.map(OrderListResDto::from);
	}

	@Transactional(readOnly = true)
	public OrderDetailResDto getOrderDetail(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
		return OrderDetailResDto.from(order);
	}

	public OrderUpdateResDto updateOrder(Long orderId, OrderUpdateReqDto request) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		Integer newTotalPrice = order.getGroupBuy().getProduct().getPrice() * request.getQuantity();
		order.updateQuantityAndPrice(request.getQuantity(), newTotalPrice);

		return OrderUpdateResDto.from(order);
	}

	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
		order.cancel();
	}
}
