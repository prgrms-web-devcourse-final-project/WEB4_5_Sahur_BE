package com.team5.backend.domain.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SuppressWarnings("removal")
    @MockBean
    private DeliveryService deliveryService;

    @DisplayName("주문 기반 배송 생성 API")
    @Test
    void createDelivery() throws Exception {
        Long orderId = 1L;
        DeliveryReqDto request = new DeliveryReqDto("서울시 강남구", "01012345678", 12345);

        Delivery delivery = Delivery.builder()
                .deliveryId(1L)
                .order(null) // 테스트니까 null 허용
                .address("서울시 강남구")
                .contact("01012345678")
                .pccc(12345)
                .build();

        given(deliveryService.createDelivery(eq(orderId), any()))
                .willReturn(delivery);

        mockMvc.perform(post("/api/v1/orders/{orderId}/delivery", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.contact").value("01012345678"))
                .andExpect(jsonPath("$.pccc").value(12345));
    }

    @DisplayName("주문 기반 배송 조회 API")
    @Test
    void getDeliveryByOrder() throws Exception {
        Long orderId = 1L;

        Delivery delivery = Delivery.builder()
                .deliveryId(1L)
                .order(null)
                .address("서울시 마포구")
                .contact("01022223333")
                .pccc(98765)
                .build();

        given(deliveryService.getDeliveryByOrder(orderId)).willReturn(delivery);

        mockMvc.perform(get("/api/v1/orders/{orderId}/delivery", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("서울시 마포구"))
                .andExpect(jsonPath("$.contact").value("01022223333"))
                .andExpect(jsonPath("$.pccc").value(98765));
    }

}