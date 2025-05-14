package com.team5.backend.domain.product.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.product.search.service.ProductSearchService;
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
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductSearchService productSearchService;
    @InjectMocks
    private ProductService productService;

    private Category testCategory;
    private Product testProduct;
    private Pageable defaultPageable;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testCategory = Category.builder()
                .categoryId(1L)
                .categoryType(null)
                .keyword(null)
                .uid(123)
                .build();

        testProduct = Product.builder()
                .productId(1L)
                .category(testCategory)
                .title("테스트 상품")
                .description("설명")
                .imageUrl(List.of("http://image.url"))
                .price(1000)
                .dibCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        defaultPageable = PageRequest.of(0, 10);
    }

    @Test
    @DisplayName("상품 등록 - 성공")
    void createProduct() {
        ProductCreateReqDto req = new ProductCreateReqDto(1L, "테스트 상품", "설명", List.of("http://image.url"), 1000);

        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(productRepository.save(any(Product.class))).thenReturn(testProduct);

        ProductResDto result = productService.createProduct(req);

        assertNotNull(result);
        assertEquals(testProduct.getProductId(), result.getProductId());
        assertEquals(testProduct.getTitle(), result.getTitle());

        verify(productSearchService).index(any(Product.class));
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("전체 상품 조회 - 필터 없음")
    void getAllProducts_noFilter() {
        List<Product> productList = List.of(testProduct);
        Page<Product> mockPage = new PageImpl<>(productList, defaultPageable, productList.size());

        when(productRepository.findAllByFilter(isNull(), isNull(), eq(defaultPageable)))
                .thenReturn(mockPage);

        Page<Product> result = productService.getAllProducts(null, null, defaultPageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(testProduct.getTitle(), result.getContent().get(0).getTitle());

        verify(productRepository).findAllByFilter(isNull(), isNull(), eq(defaultPageable));
    }

    @Test
    @DisplayName("전체 상품 조회 - 카테고리 필터")
    void getAllProducts_withCategoryFilter() {
        String categoryName = "FASHION_CLOTHES";
        List<Product> productList = List.of(testProduct);
        Page<Product> mockPage = new PageImpl<>(productList, defaultPageable, productList.size());

        when(productRepository.findAllByFilter(eq(categoryName), isNull(), eq(defaultPageable)))
                .thenReturn(mockPage);

        Page<Product> result = productService.getAllProducts(categoryName, null, defaultPageable);

        assertNotNull(result);

        verify(productRepository).findAllByFilter(eq(categoryName), isNull(), eq(defaultPageable));
    }

    @Test
    @DisplayName("전체 상품 조회 - 키워드 필터")
    void getAllProducts_withKeywordFilter() {
        String keywordName = "DEFAULT";
        List<Product> productList = List.of(testProduct);
        Page<Product> mockPage = new PageImpl<>(productList, defaultPageable, productList.size());

        when(productRepository.findAllByFilter(isNull(), eq(keywordName), eq(defaultPageable)))
                .thenReturn(mockPage);

        Page<Product> result = productService.getAllProducts(null, keywordName, defaultPageable);

        assertNotNull(result);
        verify(productRepository).findAllByFilter(isNull(), eq(keywordName), eq(defaultPageable));
    }

    @Test
    @DisplayName("전체 상품 조회 - 카테고리+키워드 필터")
    void getAllProducts_withBothFilters() {
        String categoryName = "FASHION_CLOTHES";
        String keywordName = "DEFAULT";
        List<Product> productList = List.of(testProduct);
        Page<Product> mockPage = new PageImpl<>(productList, defaultPageable, productList.size());

        when(productRepository.findAllByFilter(eq(categoryName), eq(keywordName), eq(defaultPageable)))
                .thenReturn(mockPage);

        Page<Product> result = productService.getAllProducts(categoryName, keywordName, defaultPageable);

        assertNotNull(result);
        verify(productRepository).findAllByFilter(eq(categoryName), eq(keywordName), eq(defaultPageable));
    }

    @Test
    @DisplayName("상품 단건 조회 - 성공")
    void getProductById() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        ProductResDto result = productService.getProductById(1L);

        assertNotNull(result);
        assertEquals(testProduct.getTitle(), result.getTitle());
        verify(productRepository).findById(eq(1L));
    }

    @Test
    @DisplayName("상품 수정 - 성공")
    void updateProduct() {
        ProductUpdateReqDto req = new ProductUpdateReqDto("수정된 제목", "수정된 설명", List.of("수정된 이미지"), 1500);

        Product existingProduct = Product.builder()
                .productId(1L)
                .category(testCategory)
                .title("테스트 상품")
                .description("설명")
                .imageUrl(List.of("http://image.url"))
                .price(1000)
                .dibCount(0L)
                .createdAt(testProduct.getCreatedAt())
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));

        ProductResDto result = productService.updateProduct(1L, req);

        assertNotNull(result);
        assertEquals(req.getTitle(), result.getTitle());
        assertEquals(req.getDescription(), result.getDescription());
        assertEquals(req.getPrice(), result.getPrice());

        verify(productSearchService).index(any(Product.class));
        verify(productRepository).findById(eq(1L));
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

        assertEquals(testProduct.getDibCount(), count);
        verify(productRepository).findById(eq(1L));
    }
}