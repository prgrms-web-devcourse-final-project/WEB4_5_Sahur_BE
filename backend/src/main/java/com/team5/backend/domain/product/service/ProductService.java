package com.team5.backend.domain.product.service;

import com.team5.backend.domain.category.entity.Category;
import com.team5.backend.domain.category.repository.CategoryRepository;
import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductResDto createProduct(ProductCreateReqDto request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다."));

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
        return ProductResDto.fromEntity(savedProduct);
    }

    public List<ProductResDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(ProductResDto::fromEntity)
                .collect(Collectors.toList());
    }

    public ProductResDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        return ProductResDto.fromEntity(product);
    }

    public ProductResDto updateProduct(Long productId, ProductUpdateReqDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());

        Product updatedProduct = productRepository.save(product);
        return ProductResDto.fromEntity(updatedProduct);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

}
