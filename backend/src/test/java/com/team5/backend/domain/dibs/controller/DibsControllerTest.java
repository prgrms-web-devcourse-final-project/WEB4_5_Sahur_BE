package com.team5.backend.domain.dibs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.dibs.dto.DibsResDto;
import com.team5.backend.domain.dibs.service.DibsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames.TOKEN;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class DibsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SuppressWarnings("removal")
    @MockBean
    private DibsService dibsService;

    @SuppressWarnings("removal")
    @MockBean
    private ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("관심상품 등록 API")
    void createDibs() throws Exception {
        // given
        DibsResDto resDto = DibsResDto.builder()
                .dibsId(1L)
                .memberId(1L)
                .productId(100L)
                .build();

        when(dibsService.createDibs(eq(100L), eq(TOKEN))).thenReturn(resDto);

        // when & then
        mockMvc.perform(post("/api/v1/dibs/products/100")
                        .header("Authorization", TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.dibsId").value(1L))
                .andExpect(jsonPath("$.data.memberId").value(1L))
                .andExpect(jsonPath("$.data.productId").value(100L));
    }
    @Test
    @DisplayName("관심상품 삭제 API")
    void deleteDibs() throws Exception {
        // when & then
        mockMvc.perform(delete("/api/v1/dibs/products/100/dibs")
                        .param("memberId", "1"))
                .andExpect(status().isNoContent());

        verify(dibsService).deleteByProductAndToken(100L, TOKEN);
    }

    @Test
    @DisplayName("회원 관심상품 전체 조회 API")
    void getAllDibsByMember() throws Exception {
        // given
        List<DibsResDto> list = List.of(
                DibsResDto.builder().dibsId(1L).memberId(1L).productId(100L).build()
        );

        when(dibsService.getAllDibsByToken(eq(TOKEN))).thenReturn(list);

        // when & then
        mockMvc.perform(get("/api/v1/dibs/members/1/dibs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    @DisplayName("회원 관심상품 페이징 조회 API")
    void getPagedDibsByMember() throws Exception {
        // given
        Page<DibsResDto> page = new PageImpl<>(
                List.of(DibsResDto.builder().dibsId(1L).memberId(1L).productId(100L).build()),
                PageRequest.of(0, 6), 1
        );

        when(dibsService.getPagedDibsByToken(eq(TOKEN), any())).thenReturn(page);

        // when & then
        mockMvc.perform(get("/api/v1/dibs")
                        .header("Authorization", TOKEN)
                        .param("paged", "true")
                        .param("page", "0")
                        .param("size", "6"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content.length()").value(1));
    }
}