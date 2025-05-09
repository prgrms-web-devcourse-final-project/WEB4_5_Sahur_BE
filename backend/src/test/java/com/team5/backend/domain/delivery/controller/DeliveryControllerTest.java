package com.team5.backend.domain.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;

import com.team5.backend.domain.delivery.repository.DeliveryRepository;

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
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @DisplayName("POST - 배송 등록 API")
    @Test
    void createDelivery() throws Exception {
        // 생성 전 orderId에 해당하는 배송 정보 삭제
        Long orderId = 1L;
        deliveryRepository.findByOrder_OrderId(orderId)
                .ifPresent(deliveryRepository::delete);
        deliveryRepository.flush();

        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 강남구",
                12345,
                "01012345678",
                DeliveryStatus.PREPARING,
                "12345"
        );

        mockMvc.perform(post("/api/v1/deliveries/order/{orderId}", orderId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("주문별 배송 정보 등록 성공"));
    }

    @DisplayName("GET - 주문별 배송 정보 조회 API")
    @Test
    void getDeliveryByOrder() throws Exception {
        Long orderId = 1L;

        mockMvc.perform(get("/api/v1/deliveries/order/{orderId}", orderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("주문별 배송 정보 조회 성공"))
                .andExpect(jsonPath("$.data.shipping").value("TRK0000000"));
    }

    @DisplayName("GET - 배송 전체 조회 API")
    @Test
    void getAllDeliveries() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("배송 전체 조회 성공"))
                .andExpect(jsonPath("$.data.length()").value(11));
    }

    @DisplayName("PATCH - 배송 정보 수정 API")
    @Test
    void updateDelivery() throws Exception {
        Long deliveryId = 1L;
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 강남구",
                12345,
                "01012345678",
                DeliveryStatus.PREPARING,
                "TRK1234567890"
        );

        mockMvc.perform(patch("/api/v1/deliveries/{deliveryId}", deliveryId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("배송 정보 수정 성공"))
                .andExpect(jsonPath("$.data.shipping").value("TRK1234567890"));
    }

    @DisplayName("DELETE - 배송 정보 삭제 API")
    @Test
    void deleteDelivery() throws Exception {
        Long deliveryId = 1L;

        mockMvc.perform(delete("/api/v1/deliveries/{deliveryId}", deliveryId))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("배송 정보 삭제 성공"));
    }

}