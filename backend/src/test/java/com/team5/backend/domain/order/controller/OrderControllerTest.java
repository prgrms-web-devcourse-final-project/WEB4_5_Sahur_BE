package com.team5.backend.domain.order.controller;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.member.member.dto.GetMemberResDto;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.repository.OrderRepository;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @SuppressWarnings("removal")
    @MockBean
    private AuthService authService;

    Long orderId = null;

    @BeforeEach
    void setUp() {
        orderId = orderRepository.findAll().get(0).getOrderId();

        GetMemberResDto mockDto = GetMemberResDto.builder()
                .memberId(1L)
                .email("test@email.com")
                .nickname("수민짱")
                .name("테스트유저")
                .address("서울시 강남구")
                .imageUrl(null)
                .role(Role.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(authService.getLoggedInMember(anyString()))
                .willReturn(mockDto);
    }

    @Test
    @DisplayName("POST - 주문 생성 성공")
    void createOrder_success() throws Exception {
        OrderCreateReqDto request = new OrderCreateReqDto(1L, 1L, 1L, 1);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.data.quantity").value(1));
    }

    @Test
    @DisplayName("GET - 주문 목록 조회 성공")
    void getOrders_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.data.totalElements").value(20));
    }

    @Test
    @DisplayName("GET - 주문번호로 주문 목록 조회 성공")
    void getOrdersByOrderId_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("orderId", orderId.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.data.orderId").value(orderId));
    }

    @Test
    @DisplayName("GET - 주문 상태별 주문 목록 조회 성공")
    void getOrdersByStatus_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders")
                        .param("status", "PAID"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.data").value("주문 목록 조회에 성공했습니다."));
    }

    @Test
    @DisplayName("GET - 회원 주문 전체 조회")
    void getMemberOrders_all_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/me")
                        .header("Authorization", "Bearer dummy.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("회원 주문 목록 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.content[0].nickname").value("수민짱"));
    }

    @Test
    @DisplayName("GET - 상태 필터링 조회")
    void getMemberOrders_status_inProgress_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/me")
                        .header("Authorization", "Bearer dummy.jwt.token")
                        .param("status", "inProgress"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("회원 주문 목록 조회에 성공했습니다."));
    }

    @Test
    @DisplayName("GET - 이번달 총 매출 조회 성공")
    void getMonthlySales_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/monthly-sales"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @Test
    @DisplayName("GET - 주문 상세 조회 성공")
    void getOrderDetail_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("주문 상세 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.orderId").value(orderId));
    }

    @Test
    @DisplayName("PATCH - 주문 수정 성공")
    void updateOrder_success() throws Exception {
        OrderUpdateReqDto request = new OrderUpdateReqDto(3);

        mockMvc.perform(patch("/api/v1/orders/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("주문 정보가 수정되었습니다."));
    }

    @Test
    @DisplayName("DELETE - 주문 취소 성공")
    void cancelOrder_success() throws Exception {
        mockMvc.perform(delete("/api/v1/orders/{orderId}", orderId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("주문이 성공적으로 취소되었습니다."));
    }

    @Test
    @DisplayName("GET - 결제용 주문 정보 조회")
    void getOrderPage_success() throws Exception {
        mockMvc.perform(get("/api/v1/orders/{orderId}/payment", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("결제용 주문 정보 조회에 성공했습니다."))
                .andExpect(jsonPath("$.data.orderId").value(orderId));
    }

}
