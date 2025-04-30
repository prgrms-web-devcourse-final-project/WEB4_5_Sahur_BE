package com.team5.backend.domain.delivery.service;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;


@ExtendWith(MockitoExtension.class)
class DeliveryServiceTest {

    @InjectMocks
    private DeliveryService deliveryService;

    @Mock
    private DeliveryRepository deliveryRepository;

    @Mock
    private OrderRepository orderRepository;

    @DisplayName("배송 생성 성공")
    @Test
    void create() {
        // given
        Long orderId = 1L;
        DeliveryReqDto request = new DeliveryReqDto("서울시 어쩌구", "01012345678", 12345);
        Order order = Order.builder().orderId(orderId).build();

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(deliveryRepository.save(any(Delivery.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Delivery result = deliveryService.createDelivery(orderId, request);

        // then
        assertThat(result.getAddress()).isEqualTo("서울시 어쩌구");
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getPccc()).isEqualTo(12345);
    }

    @DisplayName("배송 단건 조회 성공")
    @Test
    void getByOrderId() {
        // given
        Long orderId = 2L;
        Order mockOrder = Order.builder().orderId(orderId).build();
        Delivery delivery = Delivery.create(mockOrder, "서울시 어쩌구", "01012345678",12345);

        given(deliveryRepository.findByOrderOrderId(orderId)).willReturn(Optional.of(delivery));

        // when
        Delivery result = deliveryService.getDeliveryByOrder(orderId);

        // then
        assertThat(result.getAddress()).isEqualTo("서울시 어쩌구");
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getPccc()).isEqualTo(12345);
    }



}