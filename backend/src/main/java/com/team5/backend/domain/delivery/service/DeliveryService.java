package com.team5.backend.domain.delivery.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.DeliveryErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

	private final DeliveryRepository deliveryRepository;
	private final OrderRepository orderRepository;

	public Delivery createDelivery(Long orderId, DeliveryReqDto request) {
		Order order = orderRepository.findById(orderId)
			.orElseThrow(() -> new CustomException(DeliveryErrorCode.ORDER_NOT_FOUND));
		Delivery delivery = Delivery.create(order, request);
		return deliveryRepository.save(delivery);
	}

	@Transactional(readOnly = true)
	public Delivery getDeliveryByOrder(Long orderId) {
		return deliveryRepository.findByOrder_OrderId(orderId)
			.orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
	}

	@Transactional(readOnly = true)
	public Page<Delivery> getAllDeliveries(Pageable pageable) {
		return deliveryRepository.findAll(pageable);
	}

	public Delivery updateDelivery(Long deliveryId, DeliveryReqDto request) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
			.orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
		delivery.updateDeliveryInfo(request);
		return delivery;
	}

	public void deleteDelivery(Long deliveryId) {
		Delivery delivery = deliveryRepository.findById(deliveryId)
			.orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
		deliveryRepository.delete(delivery);
	}
}
