package com.team5.backend.domain.order.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.OrderErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

	private final OrderRepository orderRepository;
	private final MemberRepository memberRepository;
	private final GroupBuyRepository groupBuyRepository;
	private final ProductRepository productRepository;

	public Order createOrder(OrderCreateReqDto request) {
		Member member = memberRepository.findById(request.getMemberId())
				.orElseThrow(() -> new CustomException(OrderErrorCode.MEMBER_NOT_FOUND));

		GroupBuy groupBuy = groupBuyRepository.findById(request.getGroupBuyId())
				.orElseThrow(() -> new CustomException(OrderErrorCode.GROUPBUY_NOT_FOUND));

		Product product = productRepository.findById(request.getProductId())
			.orElseThrow(() -> new CustomException(OrderErrorCode.PRODUCT_NOT_FOUND));

		Order order = Order.create(member, groupBuy, product, request.getQuantity());
		return orderRepository.save(order);
	}

	@Transactional(readOnly = true)
	public Page<OrderListResDto> getOrders(Long search, OrderStatus status, Pageable pageable) {
		Page<Order> orders = null;
		if (search != null) {
			orders = orderRepository.findByOrderId(search, pageable);
		} else if (status != null) {
			orders = orderRepository.findByStatus(status, pageable);
		} else {
			orders = orderRepository.findAll(pageable);
		}
		return orders.map(OrderListResDto::from);
	}

	@Transactional(readOnly = true)
	public Page<OrderListResDto> getOrdersByMember(Long memberId, String status, Pageable pageable) {
		Page<Order> orders = null;
		if ("inProgress".equalsIgnoreCase(status)) {
			List<OrderStatus> statusList = List.of(OrderStatus.BEFOREPAID, OrderStatus.PAID);
			orders = orderRepository.findByMember_MemberIdAndStatusIn(memberId, statusList, pageable);
		} else if ("canceled".equalsIgnoreCase(status)) {
			orders = orderRepository.findByMember_MemberIdAndStatusIn(memberId, List.of(OrderStatus.CANCELED), pageable);
		} else {
			orders = orderRepository.findByMember_MemberId(memberId, pageable);
		}
		return orders.map(OrderListResDto::from);
	}

	@Transactional(readOnly = true)
	public OrderDetailResDto getOrderDetail(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
		return OrderDetailResDto.from(order);
	}

	@Transactional
	public Order updateOrder(Long orderId, OrderUpdateReqDto request) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));

		int newTotalPrice = order.getProduct().getPrice() * request.getQuantity();
		order.updateOrderInfo(request.getQuantity(), newTotalPrice);

		return order;
	}

	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
		order.markAsCanceled();
	}
}
