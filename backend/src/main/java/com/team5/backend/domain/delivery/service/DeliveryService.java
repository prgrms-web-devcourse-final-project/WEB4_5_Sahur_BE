package com.team5.backend.domain.delivery.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;
	private final OrderRepository orderRepository;

	public Delivery createDelivery(Long orderId, DeliveryReqDto request) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new IllegalArgumentException("주문을 찾을 수 없습니다."));
		Delivery delivery = Delivery.create(
			order,
			request.getAddress(),
			request.getPccc(),
			request.getContact()
		);
		return deliveryRepository.save(delivery);
	}

	@Transactional(readOnly = true)
	public Delivery getDeliveryByOrder(Long orderId) {
		return deliveryRepository.findByOrderOrderId(orderId)
			.orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
	}

	public Delivery updateDelivery(Long deliveryId, DeliveryReqDto request) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
			.orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
		delivery.updateDeliveryInfo(
			request.getAddress(),
			request.getContact(),
			request.getPccc()
		);
		return delivery;
	}

	public void deleteDelivery(Long deliveryId) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
			.orElseThrow(() -> new IllegalArgumentException("배송 정보를 찾을 수 없습니다."));
		deliveryRepository.delete(delivery);
	}
}
