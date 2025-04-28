package com.team5.backend.domain.order.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
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

		Integer totalPrice = groupBuy.getProduct().getPrice() * request.getQuantity();

		Order order = Order.create(member, groupBuy, totalPrice, request.getQuantity());

		return orderRepository.save(order);
	}

	public List<OrderListResDto> getOrders() {
		return orderRepository.findAll()
			.stream()
			.map(OrderListResDto::from)
			.toList();
	}

	public OrderDetailResDto getOrderDetail(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		return OrderDetailResDto.from(order);
	}

	public Order updateOrder(Long orderId, OrderUpdateReqDto request) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		Integer newTotalPrice = order.getGroupBuy().getProduct().getPrice() * request.getQuantity();
		order.updateQuantityAndPrice(request.getQuantity(), newTotalPrice);

		if (request.getDelivery() != null) {
			order.getDelivery().updateAddressAndContact(
				request.getDelivery().getAddress(),
				request.getDelivery().getContact()
			);
		}

		return order;
	}

	public void cancelOrder(Long orderId) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));

		order.cancel();
	}
}
