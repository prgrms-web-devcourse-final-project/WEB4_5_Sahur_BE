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
import com.team5.backend.domain.order.dto.OrderCreateReqDto;
import com.team5.backend.domain.order.entity.Order;
import com.team5.backend.domain.order.repository.OrderRepository;
import com.team5.backend.domain.order.service.OrderService;

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

    @Autowired
    private OrderService orderService;

    private Order order;

    @BeforeEach
    void setUp() {
        order = orderRepository.findAll().getFirst();
    }

    @DisplayName("POST - 배송 등록 API")
    @Test
    void createDelivery() throws Exception {
        OrderCreateReqDto orderReq = new OrderCreateReqDto(1L, 1L, 3);
        order = orderService.createOrder(orderReq, 1L);

        DeliveryReqDto request = new DeliveryReqDto(
                "12345",
                "서울시 강남구",
                "테스트 123",
                12345,
                "01012345678"
        );

        mockMvc.perform(post("/api/v1/deliveries/order/{orderId}", order.getOrderId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.data.address").value("서울시 강남구 테스트 123"));
    }

    @DisplayName("GET - 주문별 배송 정보 조회 API")
    @Test
    void getDeliveryByOrder() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/order/{orderId}", order.getOrderId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.data.shipping").value("TRK0000000"));
    }

    @DisplayName("GET - 배송 전체 조회 API")
    @Test
    void getAllDeliveries() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"));
    }

    @DisplayName("GET - 배송중인 상품 개수 조회")
    @Test
    void getDeliveryCountByStatus() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.data").isNumber());
    }

    @DisplayName("PUT - 배송 정보 수정 API")
    @Test
    void updateDelivery() throws Exception {
        Long deliveryId = 1L;
        DeliveryReqDto request = new DeliveryReqDto(
                "12345",
                "서울시 강남구",
                "테스트 123",
                12345,
                "01012345678"
        );

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", deliveryId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.data.contact").value("01012345678"));
    }

    @DisplayName("PATCH - 배송 상태 변경 API")
    @Test
    void updateDeliveryStatusToNext() throws Exception {
        Long deliveryId = 1L;

        // 테스트 전에 상태 PREPARING 초기화
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        delivery.updateDeliveryStatus(DeliveryStatus.PREPARING);
        deliveryRepository.save(delivery);

        mockMvc.perform(patch("/api/v1/deliveries/{deliveryId}?status={status}", deliveryId, DeliveryStatus.INDELIVERY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"))
                .andExpect(jsonPath("$.data.afterStatus").value("INDELIVERY"));
    }

    @DisplayName("DELETE - 배송 정보 삭제 API")
    @Test
    void deleteDelivery() throws Exception {
        Long deliveryId = 1L;

        mockMvc.perform(delete("/api/v1/deliveries/{deliveryId}", deliveryId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("200"));
    }

    @DisplayName("예외 테스트 - 존재하지 않는 주문 ID")
    @Test
    void createDeliveryWithInvalidOrderId() throws Exception {
        DeliveryReqDto request = new DeliveryReqDto(
                "12345", "서울시 강남구", "테스트 123", 12345, "01012345678");

        mockMvc.perform(post("/api/v1/deliveries/order/{orderId}", 9999L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));
    }

    @DisplayName("예외 테스트 - 존재하지 않는 배송 ID 조회")
    @Test
    void getDeliveryWithInvalidId() throws Exception {
        mockMvc.perform(get("/api/v1/deliveries/order/{orderId}", 9999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));
    }

    @DisplayName("예외 테스트 - 존재하지 않는 배송 ID 수정")
    @Test
    void updateDeliveryWithInvalidId() throws Exception {
        DeliveryReqDto request = new DeliveryReqDto(
                "12345", "서울시 강남구", "테스트 123", 12345, "01012345678");

        mockMvc.perform(put("/api/v1/deliveries/{deliveryId}", 9999L)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("404"));
    }

    @DisplayName("예외 테스트 - 배송 상태 변경 실패")
    @Test
    void updateDeliveryStatusInvalidTransition() throws Exception {
        Long deliveryId = 1L;

        // 상태를 COMPLETED로 설정
        Delivery delivery = deliveryRepository.findById(deliveryId).orElseThrow();
        delivery.updateDeliveryStatus(DeliveryStatus.COMPLETED);
        deliveryRepository.save(delivery);

        mockMvc.perform(patch("/api/v1/deliveries/{deliveryId}?status={status}", deliveryId, DeliveryStatus.INDELIVERY))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("400"));
    }

}