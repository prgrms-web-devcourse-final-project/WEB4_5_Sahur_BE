package com.team5.backend.domain.product.service;

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


    @Transactional
    public ProductResDto createProduct(ProductCreateReqDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new CustomException(ProductErrorCode.CATEGORY_NOT_FOUND));

        Product product = Product.builder()
                .category(category)
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .dibCount(0L)
                .createdAt(LocalDateTime.now())
                .build();

        Product savedProduct = productRepository.save(product);
        // Optionally, you can also index the product in Elasticsearch here
        productSearchService.index(savedProduct);

        return ProductResDto.fromEntity(savedProduct);
    }

    @Transactional(readOnly = true)
    public Page<Product> getAllProducts(String category, String keyword, Pageable pageable) {
        if (category != null) {
            return productRepository.findByCategory_Category(category, pageable);
        } else if (keyword != null) {
            return productRepository.findByCategory_Keyword(keyword, pageable);
        } else {
            return productRepository.findAll(pageable);
        }
    }

    @Transactional(readOnly = true)
    public ProductResDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return ProductResDto.fromEntity(product);
    }

    @Transactional
    public ProductResDto updateProduct(Long productId, ProductUpdateReqDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));

        boolean isUpdatedForSearch =
                !product.getTitle().equals(request.getTitle()) ||
                !product.getDescription().equals(request.getDescription()) ||
                !product.getPrice().equals(request.getPrice());

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);
        if (isUpdatedForSearch) {
            productSearchService.index(updatedProduct);
        }

        return ProductResDto.fromEntity(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND);
        }
        productRepository.deleteById(productId);
        productSearchService.delete(productId);
    }

    @Transactional(readOnly = true)
    public Long getDibCount(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        return product.getDibCount();
    }
}



