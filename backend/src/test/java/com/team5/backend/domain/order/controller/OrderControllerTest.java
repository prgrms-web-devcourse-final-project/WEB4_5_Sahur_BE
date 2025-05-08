
package com.team5.backend.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.dto.OrderUpdateReqDto;
import com.team5.backend.domain.order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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

	Long orderId = null;

	@BeforeEach
	void setUp() {
		orderId = orderRepository.findAll().get(0).getOrderId();
		System.out.println(orderId);
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
			.andExpect(jsonPath("$.msg").value("주문이 성공적으로 생성되었습니다."));
	}

	@Test
	@DisplayName("GET - 주문 목록 조회 성공")
	void getOrders_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."))
			.andExpect(jsonPath("$.data.content[0].orderId").exists());
	}

	@Test
	@DisplayName("GET - 주문번호로 주문 목록 조회 성공")
	void getOrdersByOrderId_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders")
				.param("orderId", orderId.toString()))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
	}

	@Test
	@DisplayName("GET - 주문 상태별 주문 목록 조회 성공")
	void getOrdersByStatus_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders")
				.param("status", "PAID"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
	}

	@Test
	@DisplayName("GET - 회원 주문 전체 조회")
	void getMemberOrders_all_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders/members/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("회원 주문 목록 조회에 성공했습니다."))
			.andExpect(jsonPath("$.data.content[0].nickname").value("수민짱"));
	}

	@Test
	@DisplayName("GET - 상태 필터링 조회")
	void getMemberOrders_status_inProgress_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders/members/1")
				.param("status", "inProgress"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("회원 주문 목록 조회에 성공했습니다."));
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
}
