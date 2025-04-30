package com.team5.backend.domain.delivery.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.delivery.dto.DeliveryReqDto;
import com.team5.backend.domain.delivery.dto.DeliveryResDto;
import com.team5.backend.domain.delivery.entity.Delivery;
import com.team5.backend.domain.delivery.service.DeliveryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DeliveryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private DeliveryService deliveryService;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("배송 수정 API")
    @Test
    void updateDelivery() throws Exception {
        // given
        Long deliveryId = 1L;
        DeliveryReqDto request = new DeliveryReqDto("서울시 강남구", "01012345678", 12345);
        Delivery delivery = Delivery.builder()
                .deliveryId(1L)
                .order(null)
                .address("서울시 강남구")
                .contact("01012345678")
                .pccc(12345)
                .build();

        given(deliveryService.updateDelivery(eq(deliveryId), any()))
                .willReturn(delivery);

        // when & then
        mockMvc.perform(patch("/api/v1/deliveries/{deliveryId}", deliveryId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.address").value("서울시 강남구"))
                .andExpect(jsonPath("$.contact").value("01012345678"))
                .andExpect(jsonPath("$.pccc").value(12345));
    }


    @DisplayName("배송 삭제 API")
    @Test
    void deleteDelivery() throws Exception {
        // given
        Long deliveryId = 1L;

        doNothing().when(deliveryService).deleteDelivery(deliveryId);

        // when & then
        mockMvc.perform(delete("/api/v1/deliveries/{deliveryId}", deliveryId))
                .andExpect(status().isOk());
    }


    @DisplayName("배송 전체 조회 API")
    @Test
    void getAllDeliveries() throws Exception {
        // given
        List<DeliveryResDto> deliveries = List.of(
                DeliveryResDto.builder()
                        .address("서울시 강남구")
                        .contact("01012345678")
                        .pccc(12345)
                        .build(),
                DeliveryResDto.builder()
                        .address("서울시 마포구")
                        .contact("01087654321")
                        .pccc(54321)
                        .build()
        );

        given(deliveryService.getAllDeliveries()).willReturn(
                deliveries.stream().map(dto -> Delivery.builder()
                        .deliveryId(1L)
                        .order(null)
                        .address(dto.getAddress())
                        .contact(dto.getContact())
                        .pccc(dto.getPccc())
                        .build()).toList()
        );

        // when & then
        mockMvc.perform(get("/api/v1/deliveries/list"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].address").value("서울시 강남구"))
                .andExpect(jsonPath("$[0].contact").value("01012345678"))
                .andExpect(jsonPath("$[0].pccc").value(12345))
                .andExpect(jsonPath("$[1].address").value("서울시 마포구"))
                .andExpect(jsonPath("$[1].contact").value("01087654321"))
                .andExpect(jsonPath("$[1].pccc").value(54321));
    }


}