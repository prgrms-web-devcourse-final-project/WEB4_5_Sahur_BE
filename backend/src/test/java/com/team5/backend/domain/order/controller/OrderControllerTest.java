
package com.team5.backend.domain.order.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.entity.OrderStatus;
import com.team5.backend.domain.order.service.OrderService;
import com.team5.backend.domain.product.entity.Product;

@SpringBootTest
@Import(OrderControllerTest.OAuth2MockConfig.class)
@AutoConfigureMockMvc
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private OrderService orderService;

    @TestConfiguration
    static class OAuth2MockConfig {
        @Bean
        public ClientRegistrationRepository clientRegistrationRepository() {
            return Mockito.mock(ClientRegistrationRepository.class);
        }

        @Bean
        public OAuth2AuthorizedClientService oAuth2AuthorizedClientService() {
            return Mockito.mock(OAuth2AuthorizedClientService.class);
        }
    }

    private Order mockOrder() {
        Member member = Member.builder()
            .memberId(1L)
            .name("사용자")
            .nickname("닉네임")
            .build();

        Product product = Product.builder()
            .productId(3L)
            .title("상품명")
            .price(1000)
            .imageUrl("http://image.url/test.png")
            .build();

        GroupBuy groupBuy = GroupBuy.builder()
            .groupBuyId(2L)
            .build();

        return Order.builder()
            .orderId(1L)
            .member(member)
            .groupBuy(groupBuy)
            .product(product)
            .quantity(1)
            .totalPrice(1000)
            .status(OrderStatus.BEFOREPAID)
            .build();
    }

    @Test
    @DisplayName("POST /api/v1/orders - 주문 생성 성공")
    void createOrder_success() throws Exception {
        OrderCreateReqDto req = new OrderCreateReqDto(1L, 2L, 3L, 1);
        Order order = mockOrder();

        Mockito.when(orderService.createOrder(any(OrderCreateReqDto.class))).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("201"))
                .andExpect(jsonPath("$.msg").value("주문이 성공적으로 생성되었습니다."));
    }

    @Test
    @DisplayName("GET /api/v1/orders - 주문 목록 조회 성공")
    void getOrders_success() throws Exception {
        Order order = mockOrder();

        Mockito.when(orderService.getOrders(eq(null), eq(null), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(order)));

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
    }

    @Test
    @DisplayName("GET /api/v1/orders?search=1 - 주문번호로 주문 목록 조회 성공")
    void getOrdersByOrderId_success() throws Exception {
        Order order = mockOrder();
        Mockito.when(orderService.getOrders(eq(1L), eq(null), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(order)));

        mockMvc.perform(get("/api/v1/orders")
                .param("search", "1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
    }

    @Test
    @DisplayName("GET /api/v1/orders?status=PAID - 주문 상태별 주문 목록 조회 성공")
    void getOrdersByStatus_success() throws Exception {
        Order order = mockOrder();
        order.markAsPaid();

        Mockito.when(orderService.getOrders(eq(null), eq(OrderStatus.PAID), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(order)));

        mockMvc.perform(get("/api/v1/orders")
                .param("status", "PAID"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.code").value("200"))
            .andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{id} - 주문 상세 조회 성공")
    void getOrderDetail_success() throws Exception {
        Order order = mockOrder();

        Mockito.when(orderService.getOrderDetail(1L)).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("주문 상세 조회에 성공했습니다."));
    }

    @Test
    @DisplayName("PATCH /api/v1/orders/{id} - 주문 수정 성공")
    void updateOrder_success() throws Exception {
        OrderUpdateReqDto req = new OrderUpdateReqDto(3);
        Order order = mockOrder();
        order.updateOrderInfo(3, 3000);

        Mockito.when(orderService.updateOrder(eq(1L), any(OrderUpdateReqDto.class))).thenReturn(order);

        mockMvc.perform(patch("/api/v1/orders/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200"))
                .andExpect(jsonPath("$.msg").value("주문 정보가 수정되었습니다."));
    }

    @Test
    @DisplayName("DELETE /api/v1/orders/{id} - 주문 취소 성공")
    void cancelOrder_success() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/1"))
                .andExpect(status().isNoContent());

        Mockito.verify(orderService).cancelOrder(1L);
    }
}
