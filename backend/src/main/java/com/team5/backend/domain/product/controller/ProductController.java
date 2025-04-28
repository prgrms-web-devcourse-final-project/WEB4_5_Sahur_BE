package com.team5.backend.domain.product.controller;

import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResDto> createProduct(@RequestBody ProductCreateReqDto request) {
        ProductResDto response = productService.createProduct(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<ProductResDto>> getAllProducts() {
        List<ProductResDto> responses = productService.getAllProducts();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductResDto> getProductById(@PathVariable Long productId) {
        ProductResDto response = productService.getProductById(productId);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductResDto> updateProduct(@PathVariable Long productId, @RequestBody ProductUpdateReqDto request) {
        ProductResDto response = productService.updateProduct(productId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
