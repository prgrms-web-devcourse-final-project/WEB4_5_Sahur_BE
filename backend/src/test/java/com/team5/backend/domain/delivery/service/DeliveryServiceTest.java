package com.team5.backend.domain.delivery.service;

import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 어쩌구",
                12345,
                "01012345678",
                DeliveryStatus.PREPARING,
                "12345"
        );
        Order order = Order.builder().orderId(orderId).build();

        given(orderRepository.findById(orderId)).willReturn(Optional.of(order));
        given(deliveryRepository.save(any(Delivery.class))).willAnswer(invocation -> invocation.getArgument(0));

        // when
        Delivery result = deliveryService.createDelivery(orderId, request);

        // then
        assertThat(result.getAddress()).isEqualTo("서울시 어쩌구");
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getPccc()).isEqualTo(12345);
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.PREPARING);
        assertThat(result.getShipping()).isEqualTo("12345");
    }

    @DisplayName("배송 단건 조회 성공")
    @Test
    void getByOrderId() {
        // given
        Long orderId = 2L;
        Order mockOrder = Order.builder().orderId(orderId).build();
      
        Delivery delivery = Delivery.create(
                mockOrder,
                "서울시 어쩌구",
                12345,
                "01012345678",
                DeliveryStatus.PREPARING,
                "12345"
        );

        given(deliveryRepository.findByOrderOrderId(orderId)).willReturn(Optional.of(delivery));

        // when
        Delivery result = deliveryService.getDeliveryByOrder(orderId);

        // then
        assertThat(result.getAddress()).isEqualTo("서울시 어쩌구");
        assertThat(result.getPccc()).isEqualTo(12345);
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.PREPARING);
        assertThat(result.getShipping()).isEqualTo("12345");

    }


    @DisplayName("배송 수정 성공")
    @Test
    void update() {
        // given
        Long deliveryId = 3L;
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 00구",
                77777,
                "01012345678",
                DeliveryStatus.COMPLETED,
                "98765"
        );
        Order mockOrder = Order.builder().orderId(100L).build();

        Delivery delivery = Delivery.create(
                mockOrder,
                "기존주소",
                12345,
                "01011223344",
                DeliveryStatus.PREPARING,
                "11111"
        );

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.of(delivery));

        // when
        Delivery result = deliveryService.updateDelivery(deliveryId, request);

        // then
        assertThat(result.getAddress()).isEqualTo("서울시 00구");
        assertThat(result.getPccc()).isEqualTo(77777);
        assertThat(result.getContact()).isEqualTo("01012345678");
        assertThat(result.getStatus()).isEqualTo(DeliveryStatus.COMPLETED);
        assertThat(result.getShipping()).isEqualTo("98765");

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
                Delivery.create(null, "서울시 강남구", 11111, "01011112222", DeliveryStatus.PREPARING, "0001"),
                Delivery.create(null, "서울시 종로구", 22222, "01033334444", DeliveryStatus.INDELIVERY, "0002")
        );

        given(deliveryRepository.findAll()).willReturn(list);

        // when
        List<Delivery> result = deliveryService.getAllDeliveries();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getAddress()).isEqualTo("서울시 강남구");
        assertThat(result.get(0).getPccc()).isEqualTo(11111);
        assertThat(result.get(0).getContact()).isEqualTo("01011112222");
        assertThat(result.get(0).getStatus()).isEqualTo(DeliveryStatus.PREPARING);
        assertThat(result.get(0).getShipping()).isEqualTo("0001");

        assertThat(result.get(1).getAddress()).isEqualTo("서울시 종로구");
        assertThat(result.get(1).getPccc()).isEqualTo(22222);
        assertThat(result.get(1).getContact()).isEqualTo("01033334444");
        assertThat(result.get(1).getStatus()).isEqualTo(DeliveryStatus.INDELIVERY);
        assertThat(result.get(1).getShipping()).isEqualTo("0002");

    }


    @DisplayName("주문 ID가 존재하지 않으면 배송 생성 실패")
    @Test
    void createFail_OrderNotFound() {
        // given
        Long orderId = 999L;
        given(orderRepository.findById(orderId)).willReturn(Optional.empty());
      
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 00구",
                12345,
                "01000000000",
                DeliveryStatus.PREPARING,
                "0001"
        );
        // when & then
        assertThatThrownBy(() -> deliveryService.createDelivery(orderId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("주문을 찾을 수 없습니다");
    }


    @DisplayName("배송 ID가 존재하지 않으면 배송 수정 실패")
    @Test
    void updateFail_DeliveryNotFound() {
        // given
        Long deliveryId = 100L;
        DeliveryReqDto request = new DeliveryReqDto(
                "부산시 00구",
                55555,
                "01099998888",
                DeliveryStatus.INDELIVERY,
                "12121"
        );

        given(deliveryRepository.findById(deliveryId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deliveryService.updateDelivery(deliveryId, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("배송 정보를 찾을 수 없습니다");
    }

    @DisplayName("주문 ID에 해당하는 배송 정보가 없으면 조회 실패")
    @Test
    void getFail_DeliveryNotFound() {
        // given
        Long orderId = 200L;
        given(deliveryRepository.findByOrderOrderId(orderId)).willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> deliveryService.getDeliveryByOrder(orderId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("배송 정보를 찾을 수 없습니다");
    }



}