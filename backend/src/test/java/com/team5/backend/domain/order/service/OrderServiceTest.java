package com.team5.backend.domain.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.repository.GroupBuyRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.OrderErrorCode;

class OrderServiceTest {

	@InjectMocks
	private OrderService orderService;

	@Mock
	private OrderRepository orderRepository;

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private GroupBuyRepository groupBuyRepository;

	@Mock
	private ProductRepository productRepository;

	@BeforeEach
	void init() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	@DisplayName("주문 생성 성공 - 모든 ID와 수량이 유효한 경우")
	void createOrder_success() {
		Member member = Member.builder().memberId(1L).build();
		Product product = Product.builder().productId(2L).price(1000).build();
		GroupBuy groupBuy = GroupBuy.builder().groupBuyId(3L).product(product).build();

		OrderCreateReqDto request = new OrderCreateReqDto(1L, 3L, 2L, 2);

		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(groupBuyRepository.findById(3L)).thenReturn(Optional.of(groupBuy));
		when(productRepository.findById(2L)).thenReturn(Optional.of(product));
		when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Order result = orderService.createOrder(request);

		assertThat(result.getMember()).isEqualTo(member);
		assertThat(result.getGroupBuy()).isEqualTo(groupBuy);
		assertThat(result.getProduct()).isEqualTo(product);
		assertThat(result.getTotalPrice()).isEqualTo(2000);
		assertThat(result.getQuantity()).isEqualTo(2);
	}

	@Test
	@DisplayName("주문 생성 실패 - 회원 ID 없음")
	void createOrder_fail_memberNotFound() {
		OrderCreateReqDto request = new OrderCreateReqDto(1L, 3L, 2L, 2);

		when(memberRepository.findById(1L)).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.createOrder(request));
		assertEquals(OrderErrorCode.MEMBER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 생성 실패 - 공동구매 ID가 존재하지 않는 경우")
	void createOrder_fail_groupBuyNotFound() {
		OrderCreateReqDto request = new OrderCreateReqDto(1L, 999L, 2L, 2);

		Member member = Member.builder().memberId(1L).build();
		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(groupBuyRepository.findById(999L)).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.createOrder(request));
		assertEquals(OrderErrorCode.GROUPBUY_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 생성 실패 - 상품 ID가 존재하지 않는 경우")
	void createOrder_fail_productNotFound() {
		OrderCreateReqDto request = new OrderCreateReqDto(1L, 3L, 999L, 2);

		Member member = Member.builder().memberId(1L).build();
		GroupBuy groupBuy = GroupBuy.builder().groupBuyId(3L).build();

		when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
		when(groupBuyRepository.findById(3L)).thenReturn(Optional.of(groupBuy));
		when(productRepository.findById(999L)).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.createOrder(request));
		assertEquals(OrderErrorCode.PRODUCT_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 목록 조회 성공 - 페이지네이션 적용")
	void getOrders_success() {
		Order order1 = mock(Order.class);
		Order order2 = mock(Order.class);
		Page<Order> mockPage = new PageImpl<>(List.of(order1, order2));

		Pageable pageable = PageRequest.of(0, 10);
		when(orderRepository.findAll(pageable)).thenReturn(mockPage);

		Page<Order> result = orderService.getOrders(pageable);

		assertThat(result.getContent()).hasSize(2);
	}

	@Test
	@DisplayName("주문 상세 조회 성공")
	void getOrderDetail_success() {
		Order order = mock(Order.class);
		when(orderRepository.findWithDetailsByOrderId(1L)).thenReturn(Optional.of(order));

		Order result = orderService.getOrderDetail(1L);

		assertThat(result).isEqualTo(order);
	}

	@Test
	@DisplayName("주문 상세 조회 실패 - 존재하지 않는 주문")
	void getOrderDetail_fail() {
		when(orderRepository.findWithDetailsByOrderId(anyLong())).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.getOrderDetail(9L));
		assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 수정 성공 - 수량 변경에 따른 총 가격 갱신")
	void updateOrder_success() {
		Product product = Product.builder().price(1500).build();
		GroupBuy groupBuy = GroupBuy.builder().product(product).build();
		Order order = Order.builder().groupBuy(groupBuy).build();

		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

		OrderUpdateReqDto req = new OrderUpdateReqDto(3);

		Order updated = orderService.updateOrder(1L, req);

		assertThat(updated.getQuantity()).isEqualTo(3);
		assertThat(updated.getTotalPrice()).isEqualTo(4500);
	}

	@Test
	@DisplayName("주문 수정 실패 - 존재하지 않는 주문")
	void updateOrder_fail() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

		OrderUpdateReqDto req = new OrderUpdateReqDto(3);

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.updateOrder(999L, req));
		assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 취소 성공 - 주문이 존재할 경우")
	void cancelOrder_success() {
		Order order = mock(Order.class);
		when(orderRepository.findById(10L)).thenReturn(Optional.of(order));

		orderService.cancelOrder(10L);

		verify(order).markAsCanceled();
	}

	@Test
	@DisplayName("주문 취소 실패 - 주문이 존재하지 않는 경우")
	void cancelOrder_fail() {
		when(orderRepository.findById(anyLong())).thenReturn(Optional.empty());

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.cancelOrder(5L));
		assertEquals(OrderErrorCode.ORDER_NOT_FOUND, e.getErrorCode());
	}

	@Test
	@DisplayName("주문 취소 실패 - 이미 취소된 주문")
	void cancelOrder_fail_alreadyCanceled() {
		Order order = mock(Order.class);
		when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
		doThrow(new CustomException(OrderErrorCode.ORDER_ALREADY_CANCELED))
			.when(order).markAsCanceled();

		CustomException e = assertThrows(CustomException.class,
			() -> orderService.cancelOrder(1L));

		assertEquals(OrderErrorCode.ORDER_ALREADY_CANCELED, e.getErrorCode());
	}
}
