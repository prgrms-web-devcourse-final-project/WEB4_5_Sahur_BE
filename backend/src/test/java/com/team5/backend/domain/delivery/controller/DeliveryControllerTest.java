package com.team5.backend.domain.delivery.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.entity.DeliveryStatus;
import com.team5.backend.domain.delivery.repository.DeliveryRepository;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private OrderRepository orderRepository;

    private Order order;

    @BeforeEach
    void setUp() {
        order = orderRepository.findAll().getFirst();
    }

    @DisplayName("POST - 배송 등록 API")
    @Test
    void createDelivery() throws Exception {
        // 생성 전 orderId에 해당하는 배송 정보 삭제
        deliveryRepository.findByOrder_OrderId(order.getOrderId())
                .ifPresent(deliveryRepository::delete);
        deliveryRepository.flush();

        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 강남구",
                12345,
                "01012345678",
                "12345"
        );

        mockMvc.perform(post("/api/v1/deliveries/order/{orderId}", order.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("주문별 배송 정보 등록 성공"));
    }

    @DisplayName("GET - 주문별 배송 정보 조회 API")
    @Test
    void getDeliveryByOrder() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/order/{orderId}", order.getOrderId()))
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

    @DisplayName("PUT - 배송 정보 수정 API")
    @Test
    void updateDelivery() throws Exception {
        Long deliveryId = 1L;
        DeliveryReqDto request = new DeliveryReqDto(
                "서울시 강남구",
                12345,
                "01012345678",
                "TRK1234567890"
        );

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", deliveryId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("배송 정보 수정 성공"))
                .andExpect(jsonPath("$.data.shipping").value("TRK1234567890"));
    }

    @DisplayName("PATCH - 배송 상태 변경 API")
    @Test
    void updateDeliveryStatusToNext() throws Exception {
        Long deliveryId = 1L;

        // 테스트 전에 상태 PREPARING 초기화
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        delivery.updateDeliveryStatus(DeliveryStatus.PREPARING);
        deliveryRepository.save(delivery);

        mockMvc.perform(patch("/api/v1/deliveries/{deliveryId}", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value("200-0"))
                .andExpect(jsonPath("$.msg").value("배송 상태 변경 완료"))
                .andExpect(jsonPath("$.data").value("INDELIVERY"));
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