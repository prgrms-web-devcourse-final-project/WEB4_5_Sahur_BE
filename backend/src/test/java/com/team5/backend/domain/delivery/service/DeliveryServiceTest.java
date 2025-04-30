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

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


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


    @DisplayName("배송 수정 성공")
    @Test
    void update() {
        // given
        Long deliveryId = 3L;
        DeliveryReqDto request = new DeliveryReqDto("서울시 00구", "01012345678", 77777);
        Order mockOrder = Order.builder().orderId(100L).build();
        Delivery delivery = Delivery.create(mockOrder, "기존주소", "01011223344", 12345);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // when
        Delivery result = deliveryService.updateDelivery(deliveryId, request);

        // then
        assertThat(result.getAddress()).isEqualTo("서울시 00구");
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getPccc()).isEqualTo(77777);
    }

    @DisplayName("배송 삭제 성공")
    @Test
    void delete() {
        // given
        Long deliveryId = 4L;
        Delivery delivery = mock(Delivery.class);

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // when
        deliveryService.deleteDelivery(deliveryId);

        // then
        verify(deliveryRepository).delete(delivery);
    }

    @DisplayName("배송 전체 조회 성공")
    @Test
    void getAll() {
        // given
        List<Delivery> list = List.of(
                Delivery.create(null, "서울시 강남구", "01011112222", 11111),
                Delivery.create(null, "서울시 종로구", "01033334444", 22222)
        );

        given(deliveryRepository.findAll()).willReturn(list);

        // when
        List<Delivery> result = deliveryService.getAllDeliveries();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAddress()).isEqualTo("서울시 강남구");
        assertThat(result.get(1).getAddress()).isEqualTo("서울시 종로구");
    }

}