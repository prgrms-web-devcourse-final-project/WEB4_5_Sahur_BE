package com.team5.backend.domain.delivery.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.order.service.OrderService;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.DeliveryErrorCode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@Transactional
class DeliveryServiceTest {

    @Autowired
    private DeliveryService deliveryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @Autowired
    private OrderRepository orderRepository;

    Long deliveryId = 1L;

    @Test
    @DisplayName("배송 등록 성공")
    void createDelivery_success() {
        OrderCreateReqDto orderReq = new OrderCreateReqDto(1L, 1L, 1L, 3);
        Order order = orderService.createOrder(orderReq);

        DeliveryReqDto deliveryReq = new DeliveryReqDto(
                "서울시 어쩌구",
                12345,
                "01012345678",
                "12345"
        );
        Delivery delivery = deliveryService.createDelivery(order.getOrderId(), deliveryReq);

        assertThat(delivery.getAddress()).isEqualTo("서울시 어쩌구");
        assertThat(delivery.getPccc()).isEqualTo(12345);
        assertThat(delivery.getContact()).isEqualTo("01012345678");
        assertThat(delivery.getStatus()).isEqualTo(DeliveryStatus.PREPARING);
        assertThat(delivery.getShipping()).isEqualTo("12345");
    }

    @Test
    @DisplayName("배송 등록 실패 - 존재하지 않는 주문 ID로 요청")
    void createDelivery_fail() {
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 어쩌구",
                12345,
                "01012345678",
                "12345"
        );

        CustomException e = assertThrows(CustomException.class,
                () -> deliveryService.createDelivery(999L, request));
        assertEquals(DeliveryErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문별 배송 정보 조회 성공")
    void getDeliveryByOrder_success() {
        Long orderId = orderRepository.findAll().getFirst().getOrderId();
        Delivery delivery = deliveryService.getDeliveryByOrder(orderId);

        assertThat(delivery).isNotNull();
        assertThat(delivery.getOrder().getOrderId()).isEqualTo(orderId);
    }

    @Test
    @DisplayName("주문별 배송 정보 조회 실패 - 존재하지 않는 배송 정보")
    void getDeliveryByOrder_fail() {
        OrderCreateReqDto request = new OrderCreateReqDto(1L, 1L, 1L, 3);
        Order order = orderService.createOrder(request);

        CustomException e = assertThrows(CustomException.class,
                () -> deliveryService.getDeliveryByOrder(order.getOrderId()));
        assertEquals(DeliveryErrorCode.DELIVERY_NOT_FOUND, e.getErrorCode());
    }


    @Test
    @DisplayName("배송 전체 조회 성공")
    void getAllDeliveries_success() {
        Page<Delivery> result = deliveryService.getAllDeliveries(PageRequest.of(0, 10));
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("배송중인 상품 개수 조회 성공")
    void countDeliveriesByStatus_success() {
        DeliveryStatus status = DeliveryStatus.INDELIVERY;
        long count = deliveryService.countDeliveriesByStatus(status);

        assertThat(count).isInstanceOf(Long.class);
        assertThat(count).isGreaterThanOrEqualTo(0);
    }

    @Test
    @DisplayName("배송 정보 수정 성공")
    void updateDelivery_success() {
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 00구",
                77777,
                "01012345678",
                "98765"
        );
        Delivery result = deliveryService.updateDeliveryInfo(deliveryId, request);

        assertThat(result.getAddress()).isEqualTo("서울시 00구");
        assertThat(result.getPccc()).isEqualTo(77777);
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getShipping()).isEqualTo("98765");
    }

    @Test
    @DisplayName("배송 정보 수정 실패 - 존재하지 않는 배송 ID로 요청")
    void updateDelivery_fail() {
        DeliveryReqDto request = new DeliveryReqDto(
                "부산시 00구",
                55555,
                "01099998888",
                "12121"
        );

        CustomException e = assertThrows(CustomException.class,
                () -> deliveryService.updateDeliveryInfo(999L, request));
        assertEquals(DeliveryErrorCode.DELIVERY_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("배송 상태 전이 성공 - INDELIVERY → COMPLETED")
    void updateDeliveryStatusToNext_success_inDeliveryToCompleted() {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        delivery.updateDeliveryStatus(DeliveryStatus.INDELIVERY);

        DeliveryStatus result = deliveryService.updateDeliveryStatus(deliveryId);

        assertThat(result).isEqualTo(DeliveryStatus.COMPLETED);
    }

    @Test
    @DisplayName("배송 상태 전이 실패 - COMPLETED 상태에서 다음 단계 없음")
    void updateDeliveryStatusToNext_fail_invalidTransition() {
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        delivery.updateDeliveryStatus(DeliveryStatus.COMPLETED);

        CustomException e = assertThrows(CustomException.class,
                () -> deliveryService.updateDeliveryStatus(deliveryId));

        assertEquals(DeliveryErrorCode.INVALID_STATUS_TRANSITION, e.getErrorCode());
    }

    @Test
    @DisplayName("배송 정보 삭제 성공")
    void deleteDelivery_success() {
        deliveryService.deleteDelivery(deliveryId);
        assertThat(deliveryRepository.findById(deliveryId)).isEmpty();
    }

    @Test
    @DisplayName("배송 정보 삭제 실패 - 존재하지 않는 배송 ID로 요청")
    void deleteDelivery_fail() {
        CustomException e = assertThrows(CustomException.class,
                () -> deliveryService.deleteDelivery(999L));
        assertEquals(DeliveryErrorCode.DELIVERY_NOT_FOUND, e.getErrorCode());
    }
}