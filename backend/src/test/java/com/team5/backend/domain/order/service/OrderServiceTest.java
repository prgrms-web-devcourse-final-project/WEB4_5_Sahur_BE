package com.team5.backend.domain.order.service;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderDetailResDto;
import com.team5.backend.domain.order.dto.OrderListResDto;
import com.team5.backend.domain.order.dto.OrderPaymentInfoResDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.OrderErrorCode;

import jakarta.transaction.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private GroupBuyRepository groupBuyRepository;

    private Member member;
    private Product product;
    private GroupBuy groupBuy;
    private Order order;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        member = memberRepository.findAll().getFirst();
        product = productRepository.findAll().getFirst();
        groupBuy = groupBuyRepository.findAll().getFirst();
        order = orderRepository.findAll().getFirst();
        pageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("주문 생성 성공")
    void createOrder_success() {
        OrderCreateReqDto request = new OrderCreateReqDto(
                member.getMemberId(),
                groupBuy.getGroupBuyId(),
                product.getProductId(),
                3
        );

        Order newOrder = orderService.createOrder(request);

        assertThat(newOrder.getOrderId()).isNotNull();
        assertThat(newOrder.getQuantity()).isEqualTo(3);
        assertThat(newOrder.getTotalPrice()).isEqualTo(product.getPrice() * 3);
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 회원")
    void createOrder_fail_memberNotFound() {
        OrderCreateReqDto request = new OrderCreateReqDto(
                999L, groupBuy.getGroupBuyId(), product.getProductId(), 1
        );

        CustomException e = assertThrows(CustomException.class,
                () -> orderService.createOrder(request));
        assertEquals(OrderErrorCode.MEMBER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 공동구매")
    void createOrder_fail_groupBuyNotFound() {
        OrderCreateReqDto request = new OrderCreateReqDto(
                member.getMemberId(), 999L, product.getProductId(), 1
        );

        CustomException e = assertThrows(CustomException.class,
                () -> orderService.createOrder(request));
        assertEquals(OrderErrorCode.GROUPBUY_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 생성 실패 - 존재하지 않는 상품")
    void createOrder_fail_productNotFound() {
        OrderCreateReqDto request = new OrderCreateReqDto(
                member.getMemberId(), groupBuy.getGroupBuyId(), 999L, 1
        );

        CustomException e = assertThrows(CustomException.class,
                () -> orderService.createOrder(request));
        assertEquals(OrderErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 목록 조회 성공")
    void getOrders_success() {
        Page<OrderListResDto> result = orderService.getOrders(null, null, pageable);
        assertThat(result.getContent()).isNotEmpty();
    }

    @Test
    @DisplayName("주문 목록 조회 - 주문번호로 필터링 성공")
    void getOrders_byOrderId_success() {
        Long orderId = order.getOrderId();
        Page<OrderListResDto> result = orderService.getOrders(orderId, null, pageable);

        assertThat(result.getContent())
                .hasSize(1)
                .extracting(OrderListResDto::getOrderId)
                .containsExactly(orderId);
    }

    @Test
    @DisplayName("주문 목록 조회 - 상태로 필터링 성공")
    void getOrders_byStatus_success() {
        Page<OrderListResDto> result = orderService.getOrders(null, OrderStatus.BEFOREPAID, pageable);

        assertThat(result.getContent())
                .isNotEmpty()
                .allMatch(o -> o.getStatus() == OrderStatus.BEFOREPAID);
    }

    @Test
    @DisplayName("회원 주문 조회 성공 - 전체 상태")
    void getOrdersByMember_all_success() {
        Page<OrderListResDto> result = orderService.getOrdersByMember(member.getMemberId(), null, pageable);

        assertThat(result.getContent())
                .isNotEmpty()
                .allMatch(o -> o.getMemberId().equals(member.getMemberId()));
    }

    @Test
    @DisplayName("회원 주문 조회 성공 - inProgress 상태 필터링")
    void getOrdersByMember_inProgress_success() {
        Page<OrderListResDto> result = orderService.getOrdersByMember(member.getMemberId(), "inProgress", pageable);

        assertThat(result.getContent())
                .isNotEmpty()
                .allMatch(o -> List.of(OrderStatus.BEFOREPAID, OrderStatus.PAID).contains(o.getStatus()));
    }

    @Test
    @DisplayName("회원 주문 조회 성공 - canceled 상태 필터링")
    void getOrdersByMember_canceled_success() {
        // 미리 취소된 주문 생성
        orderRepository.save(Order.builder()
                .orderId(1L)
                .member(member)
                .product(product)
                .groupBuy(groupBuy)
                .quantity(1)
                .totalPrice(product.getPrice())
                .status(OrderStatus.CANCELED)
                .build());

        Page<OrderListResDto> result = orderService.getOrdersByMember(member.getMemberId(), "canceled", pageable);

        assertThat(result.getContent())
                .extracting(OrderListResDto::getStatus)
                .contains(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("주문 상세 조회 성공")
    void getOrderDetail_success() {
        OrderDetailResDto response = orderService.getOrderDetail(order.getOrderId());
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getOrderId());
    }

    @Test
    @DisplayName("주문 상세 조회 실패 - 존재하지 않는 주문")
    void getOrderDetail_fail() {
        CustomException e = assertThrows(CustomException.class,
                () -> orderService.getOrderDetail(999L));
        assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 수정 성공")
    void updateOrder_success() {
        OrderUpdateReqDto req = new OrderUpdateReqDto(5);
        Order updated = orderService.updateOrder(order.getOrderId(), req);
        assertThat(updated.getQuantity()).isEqualTo(5);
        assertThat(updated.getTotalPrice()).isEqualTo(product.getPrice() * 5);
    }

    @Test
    @DisplayName("주문 수정 실패 - 존재하지 않는 주문")
    void updateOrder_fail() {
        OrderUpdateReqDto req = new OrderUpdateReqDto(2);
        CustomException e = assertThrows(CustomException.class,
                () -> orderService.updateOrder(999L, req));
        assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 취소 성공")
    void cancelOrder_success() {
        orderService.cancelOrder(order.getOrderId());
        Order canceled = orderRepository.findById(order.getOrderId())
                .orElseThrow(() -> new CustomException(OrderErrorCode.ORDER_NOT_FOUND));
        assertThat(canceled.getStatus()).isEqualTo(OrderStatus.CANCELED);
    }

    @Test
    @DisplayName("주문 취소 실패 - 존재하지 않는 주문")
    void cancelOrder_fail() {
        CustomException e = assertThrows(CustomException.class,
                () -> orderService.cancelOrder(999L));
        assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
    }

    @Test
    @DisplayName("주문 취소 실패 - 이미 취소된 주문")
    void cancelOrder_fail_alreadyCanceled() {
        order.markAsCanceled();
        orderRepository.save(order);

        CustomException e = assertThrows(CustomException.class,
                () -> orderService.cancelOrder(order.getOrderId()));

        assertEquals(OrderErrorCode.ORDER_ALREADY_CANCELED, e.getErrorCode());
    }

    @Test
    @DisplayName("결제용 주문 정보 조회 성공")
    void getOrderPaymentInfo_success() {
        OrderPaymentInfoResDto response = orderService.getOrderPaymentInfo(order.getOrderId());
        assertThat(response).isNotNull();
        assertThat(response.getOrderId()).isEqualTo(order.getOrderId());
    }

    @Test
    @DisplayName("결제용 주문 정보 조회 실패 - 존재하지 않는 주문")
    void getOrderPaymentInfo_fail() {
        CustomException e = assertThrows(CustomException.class,
                () -> orderService.getOrderPaymentInfo(999L));
        assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
    }
}
