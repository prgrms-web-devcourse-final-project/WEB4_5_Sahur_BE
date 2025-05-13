package com.team5.backend.domain.product.service;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.product.search.repository.ProductSearchRepository;
import com.team5.backend.domain.product.search.service.ProductSearchService;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.ProductErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductSearchRepository productSearchRepository;
    private final ProductSearchService productSearchService;


    /**
     * 상품 등록
     */
    @Transactional
    public ProductResDto createProduct(ProductCreateReqDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.create(
                category,
                request.getTitle(),
                request.getDescription(),
                request.getImageUrl(),
                request.getPrice()
        );

        Product savedProduct = productRepository.save(product);
        // Optionally, you can also index the product in Elasticsearch here
        productSearchService.index(savedProduct);

        return ProductResDto.fromEntity(savedProduct);
    }

    /**
     * 전체 상품 조회 (카테고리 또는 키워드 기준 필터링 포함, 페이징)
     */
    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String category, String keyword, Pageable pageable) {
        return productRepository.findAllByFilter(category, keyword, pageable);
    }

    /**
     * 단건 상품 조회
     */
    @Transactional(readOnly = true)
    public ProductResDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductResDto.fromEntity(product);
    }

    /**
     * 상품 정보 수정
     */
    @Transactional
    public ProductResDto updateProduct(Long productId, ProductUpdateReqDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        boolean isUpdatedForSearch =
                !product.getTitle().equals(request.getTitle()) ||
                !product.getDescription().equals(request.getDescription()) ||
                !product.getPrice().equals(request.getPrice());

        product.update(
                request.getTitle(),
                request.getDescription(),
                request.getImageUrl(),
                request.getPrice()
        );

        if (isUpdatedForSearch) {
            productSearchService.index(product);
        }

        return ProductResDto.fromEntity(product);
    }

    /**
     * 상품 삭제
     */
    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(productId);
    }

    /**
     * 찜 수 조회
     */
    @Transactional(readOnly = true)
    public Long getDibCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return product.getDibCount();
    }
}



