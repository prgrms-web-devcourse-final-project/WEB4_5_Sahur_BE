package com.team5.backend.domain.delivery.service;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryStatusUpdateReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryStatusUpdateResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.notification.redis.NotificationPublisher;
import com.team5.backend.domain.notification.template.NotificationTemplateType;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.DeliveryErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final OrderRepository orderRepository;
    private final ShippingGenerator shippingGenerator;

    private final NotificationPublisher notificationPublisher;

    public Delivery createDelivery(Long orderId, DeliveryReqDto request) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.ORDER_NOT_FOUND));
        String shipping = shippingGenerator.generateShipping();
        Delivery delivery = Delivery.create(order, shipping, request);
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

    public long countDeliveriesByStatus(DeliveryStatus status) {
        return deliveryRepository.countByStatus(status);
    }

    public Delivery updateDeliveryInfo(Long deliveryId, DeliveryReqDto request) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
        delivery.updateDeliveryInfo(request);
        return delivery;
    }

    public DeliveryStatusUpdateResDto updateDeliveryStatus(Long deliveryId, DeliveryStatus status) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

        DeliveryStatus current = delivery.getStatus();

        if (!current.canTransitionTo(status)) {
            throw new CustomException(DeliveryErrorCode.INVALID_STATUS_TRANSITION);
        }

        delivery.updateDeliveryStatus(status);
        delivery.getOrder().setDelivery(delivery);  // 동기화
        notifyDeliveryStatus(delivery);

        return new DeliveryStatusUpdateResDto(deliveryId, current, status, "성공");
    }

    public List<DeliveryStatusUpdateResDto> updateDeliveryStatuses(DeliveryStatusUpdateReqDto request) {
        List<DeliveryStatusUpdateResDto> results = new ArrayList<>();

        for (Long orderId : request.orderIds()) {
            try {
                Delivery delivery = deliveryRepository.findByOrder_OrderId(orderId)
                        .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));

                DeliveryStatus current = delivery.getStatus();
                DeliveryStatus next = current.next()
                        .orElseThrow(() -> new CustomException(DeliveryErrorCode.INVALID_STATUS_TRANSITION));

                delivery.updateDeliveryStatus(next);
                delivery.getOrder().setDelivery(delivery);  // 동기화
                notifyDeliveryStatus(delivery);

                results.add(new DeliveryStatusUpdateResDto(orderId, current, next, "성공"));
            } catch (CustomException e) {
                results.add(new DeliveryStatusUpdateResDto(orderId, null, null, "실패: " + e.getMessage()));
            }
        }

        return results;
    }

    public void deleteDelivery(Long deliveryId) {
        Delivery delivery = deliveryRepository.findById(deliveryId)
                .orElseThrow(() -> new CustomException(DeliveryErrorCode.DELIVERY_NOT_FOUND));
        deliveryRepository.delete(delivery);
    }

    public void notifyDeliveryStatus(Delivery delivery) {
        Long orderId = delivery.getOrder().getOrderId();
        switch (delivery.getStatus()) {
            case INDELIVERY -> notificationPublisher.publish(NotificationTemplateType.IN_DELIVERY, orderId);
            case COMPLETED -> notificationPublisher.publish(NotificationTemplateType.DELIVERY_DONE, orderId);
        }
    }
}
