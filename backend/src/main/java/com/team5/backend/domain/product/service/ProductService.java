package com.team5.backend.domain.product.service;

import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.entity.Product.ProductStatus;
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

    public ProductResDto createProduct(ProductCreateReqDto request) {
        Product product = Product.builder()
                .memberId(request.getMemberId())
                .title(request.getTitle())
                .description(request.getDescription())
                .imageUrl(request.getImageUrl())
                .price(request.getPrice())
                .dibCount(0L)
                .createdAt(LocalDateTime.now())
                .status(ProductStatus.WAITING)
                .build();

        Product savedProduct = productRepository.save(product);
        return toResponse(savedProduct);
    }

    public List<ProductResDto> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ProductResDto getProductById(Long productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        return toResponse(product);
    }

    public ProductResDto updateProduct(Long productId, ProductUpdateReqDto request) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다. ID: " + productId));

        product.setTitle(request.getTitle());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setPrice(request.getPrice());
        product.setStatus(request.getStatus());

        Product updatedProduct = productRepository.save(product);
        return toResponse(updatedProduct);
    }

    public void deleteProduct(Long productId) {
        productRepository.deleteById(productId);
    }

    private ProductResDto toResponse(Product product) {
        return ProductResDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .dibCount(product.getDibCount())
                .createdAt(product.getCreatedAt())
                .status(product.getStatus())
                .build();
    }
}
