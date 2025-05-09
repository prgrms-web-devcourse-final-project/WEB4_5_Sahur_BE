package com.team5.backend.domain.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.service.ProductService;
import com.team5.backend.global.util.JwtUtil;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Transactional
@Import(ProductControllerTest.MockConfig.class)
class ProductControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ProductService productService;

    @TestConfiguration
    static class MockConfig {
        @Bean
        public ProductService productService() {
            return Mockito.mock(ProductService.class);
        }

        @Bean
        public JwtUtil jwtUtil() {
            return Mockito.mock(JwtUtil.class);
        }
    }

    @Test
    @DisplayName("상품 등록 - 성공")
    void createProduct() throws Exception {
        ProductCreateReqDto req = new ProductCreateReqDto(1L, "테스트", "설명", List.of("이미지1", "이미지2"), 1000);
        ProductResDto res = ProductResDto.builder().productId(1L).title("테스트").build();

        Mockito.when(productService.createProduct(any())).thenReturn(res);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("상품 생성 성공"))
                .andExpect(jsonPath("$.data.productId").value(1L));
    }

    @Test
    @DisplayName("상품 전체 조회")
    void getAllProducts() throws Exception {
        Product product = Product.builder()
                .productId(1L)
                .title("상품")
                .category(Category.builder().categoryId(1L).uid(100).build())
                .build();

        Page<Product> page = new PageImpl<>(List.of(product));

        Mockito.when(productService.getAllProducts(any(), any(), any())).thenReturn(page);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"));
    }

    @Test
    @DisplayName("상품이 없을 때 빈 리스트 응답")
    void getAllProducts_EmptyList() throws Exception {
        Page<Product> emptyPage = new PageImpl<>(List.of());

        Mockito.when(productService.getAllProducts(any(), any(), any())).thenReturn(emptyPage);

        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("상품 목록 조회 성공"))
                .andExpect(jsonPath("$.data.content").isEmpty());
    }

    @Test
    @DisplayName("상품 단건 조회")
    void getProductById() throws Exception {
        ProductResDto dto = ProductResDto.builder().productId(1L).title("상품").build();
        Mockito.when(productService.getProductById(1L)).thenReturn(dto);

        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(1L));
    }

    @Test
    @DisplayName("상품 수정")
    void updateProduct() throws Exception {
        ProductUpdateReqDto req = new ProductUpdateReqDto("수정", "설명", List.of("이미지1", "이미지2"), 2000);
        ProductResDto dto = ProductResDto.builder().productId(1L).title("수정").build();

        Mockito.when(productService.updateProduct(eq(1L), any())).thenReturn(dto);

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("상품 수정 성공"));
    }

    @Test
    @DisplayName("상품 삭제")
    void deleteProduct() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1").with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.msg").value("상품 삭제 성공"));
    }

    @Test
    @DisplayName("찜 수 조회")
    void getDibCount() throws Exception {
        Mockito.when(productService.getDibCount(1L)).thenReturn(5L);

        mockMvc.perform(get("/api/v1/products/1/dibs/count"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(5L));
    }
}
