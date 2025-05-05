
package com.team5.backend.domain.order.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

	@Autowired
	private MockMvc mockMvc;

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

	@Test
	@DisplayName("POST /api/v1/orders - 주문 생성 성공")
	void createOrder_success() throws Exception {
		String json = """
            {
              \"memberId\": 1,
              \"groupBuyId\": 1,
              \"productId\": 1,
              \"quantity\": 1
            }
        """;

		mockMvc.perform(post("/api/v1/orders")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문이 성공적으로 생성되었습니다."));
	}

	@Test
	@DisplayName("GET /api/v1/orders - 주문 목록 조회 성공")
	void getOrders_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."))
			.andExpect(jsonPath("$.data.content[0].orderId").exists());
	}

	@Test
	@DisplayName("GET /api/v1/orders?search=1 - 주문번호로 주문 목록 조회 성공")
	void getOrdersByOrderId_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders")
				.param("search", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
	}

	@Test
	@DisplayName("GET /api/v1/orders?status=PAID - 주문 상태별 주문 목록 조회 성공")
	void getOrdersByStatus_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders")
				.param("status", "PAID"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 목록 조회에 성공했습니다."));
	}

	@Test
	@DisplayName("GET /api/v1/orders/members/{memberId} - 회원 주문 전체 조회")
	void getMemberOrders_all_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders/members/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("회원 주문 목록 조회에 성공했습니다."))
			.andExpect(jsonPath("$.data.content[0].nickname").value("길동이"));
	}

	@Test
	@DisplayName("GET /api/v1/orders/members/{memberId}?status=inProgress - 상태 필터링 조회")
	void getMemberOrders_status_inProgress_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders/members/1")
				.param("status", "inProgress"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("회원 주문 목록 조회에 성공했습니다."));
	}

	@Test
	@DisplayName("GET /api/v1/orders/{id} - 주문 상세 조회 성공")
	void getOrderDetail_success() throws Exception {
		mockMvc.perform(get("/api/v1/orders/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 상세 조회에 성공했습니다."))
			.andExpect(jsonPath("$.data.orderId").value(1));
	}

	@Test
	@DisplayName("PATCH /api/v1/orders/{id} - 주문 수정 성공")
	void updateOrder_success() throws Exception {
		String json = """
            {
              \"quantity\": 3
            }
        """;

		mockMvc.perform(patch("/api/v1/orders/1")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문 정보가 수정되었습니다."));
	}

	@Test
	@DisplayName("DELETE /api/v1/orders/{id} - 주문 취소 성공")
	void cancelOrder_success() throws Exception {
		mockMvc.perform(delete("/api/v1/orders/1"))
			.andExpect(status().isNoContent())
			.andExpect(jsonPath("$.code").value("200-0"))
			.andExpect(jsonPath("$.msg").value("주문이 성공적으로 취소되었습니다."));
	}
}
