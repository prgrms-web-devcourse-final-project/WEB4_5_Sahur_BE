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
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.OrderErrorCode;

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
				.orElseThrow(() -> new CustomException(OrderErrorCode.MEMBER_NOT_FOUND));

		GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyId())
				.orElseThrow(() -> new CustomException(OrderErrorCode.GROUPBUY_NOT_FOUND));

		Order order = Order.create(member, groupBuy, request.getQuantity());
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public Page<Order> getOrders(Pageable pageable) {
		return orderRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Order getOrderDetail(Long orderId) {
		return orderRepository.findWithDetailsById(orderId)
			.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
	}

	public Order updateOrder(Long orderId, OrderUpdateReqDto request) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

		Integer newTotalPrice = order.getGroupBuy().getProduct().getPrice() * request.getQuantity();
		order.updateOrderInfo(request.getQuantity(), newTotalPrice);

		return order;
	}

	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
		order.markAsCanceled();
	}
}
