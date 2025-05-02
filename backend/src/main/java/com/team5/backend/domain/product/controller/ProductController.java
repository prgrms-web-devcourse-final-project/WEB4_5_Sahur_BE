package com.team5.backend.domain.product.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public RsData<Page<ProductResDto>> getAllProducts(
        @RequestParam(required = false) String category,
        @RequestParam(required = false) String keyword,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Product> page = productService.getAllProducts(category, keyword, pageable);
        Page<ProductResDto> response = page.map(ProductResDto::fromEntity);
        return new RsData<>("200", "상품 목록을 조회했습니다.", response);
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
