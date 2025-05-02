package com.team5.backend.domain.product.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.service.ProductService;
import com.team5.backend.global.dto.RsData;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResDto> createProduct(@RequestBody @Valid ProductCreateReqDto request) {
        ProductResDto response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public RsData<List<ProductResDto>> getAllProducts(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String keyword
    ) {
        List<Product> products = productService.getAllProducts(category, keyword);
        List<ProductResDto> response = products.stream()
            .map(ProductResDto::fromEntity)
            .collect(Collectors.toList());

        String msg = response.isEmpty() ? "조회된 상품이 없습니다." : "상품 목록을 조회했습니다.";
        return new RsData<>("200", msg, response);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResDto> getProductById(@PathVariable Long productId) {
        ProductResDto response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid ProductUpdateReqDto request) {
        ProductResDto response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
