package com.team5.backend.domain.product.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductService productService;

    private Category testCategory;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = Category.builder()
                .categoryId(1L)
                .category(null)
                .keyword(null)
                .uid(123)
                .build();

        testProduct = Product.builder()
                .productId(1L)
                .category(testCategory)
                .title("테스트 상품")
                .description("설명")
                .imageUrl("http://image.url")
                .price(1000)
                .dibCount(0L)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("상품 등록 - 성공")
    void createProduct() {
        ProductCreateReqDto req = new ProductCreateReqDto(1L, "테스트", "설명", "이미지", 1000);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any())).thenReturn(testProduct);

        var result = productService.createProduct(req);

        assertNotNull(result);
        assertEquals("테스트 상품", result.getTitle());
    }

    @Test
    @DisplayName("전체 상품 조회 - 카테고리 없음, 키워드 없음 -> 전체 반환")
    void getAllProducts() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(testProduct));

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getAllProducts(null, null, pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("상품 단건 조회 - 성공")
    void getProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        var result = productService.getProductById(1L);

        assertEquals("테스트 상품", result.getTitle());
    }

    @Test
    @DisplayName("상품 수정 - 성공")
    void updateProduct() {
        ProductUpdateReqDto req = new ProductUpdateReqDto("수정된 제목", "수정된 설명", "수정이미지", 1500);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(productRepository.save(any())).thenReturn(testProduct);

        var result = productService.updateProduct(1L, req);

        assertEquals("수정된 제목", result.getTitle());
        verify(productRepository).save(testProduct);
    }

    @Test
    @DisplayName("상품 삭제 - 성공")
    void deleteProduct() {
        when(productRepository.existsById(1L)).thenReturn(true);

        productService.deleteProduct(1L);

        verify(productRepository).deleteById(1L);
    }

    @Test
    @DisplayName("찜 수 조회 - 정상 반환")
    void getDibCount() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        Long count = productService.getDibCount(1L);

        assertEquals(0L, count);
    }
}