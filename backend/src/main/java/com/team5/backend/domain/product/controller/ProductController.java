package com.team5.backend.domain.product.controller;

import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.service.ProductService;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 생성", description = "새로운 상품을 등록합니다.")
    @PostMapping
    public RsData<ProductResDto> createProduct(@RequestBody @Valid ProductCreateReqDto request) {
        ProductResDto response = productService.createProduct(request);
        return RsDataUtil.success("상품 생성 성공", response);
    }

    @Operation(summary = "상품 목록 조회", description = "카테고리 또는 키워드로 상품을 검색하고, 페이징 처리된 결과를 반환합니다.")
    @GetMapping
    public RsData<Page<ProductResDto>> getAllProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String keyword,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<Product> page = productService.getAllProducts(category, keyword, pageable);
        Page<ProductResDto> response = page.map(ProductResDto::fromEntity);
        return RsDataUtil.success("상품 목록 조회 성공", response);
    }

    @Operation(summary = "상품 단건 조회", description = "상품 ID로 특정 상품을 조회합니다.")
    @GetMapping("/{productId}")
    public RsData<ProductResDto> getProductById(@PathVariable Long productId) {
        ProductResDto response = productService.getProductById(productId);
        return RsDataUtil.success("상품 조회 성공", response);
    }

    @Operation(summary = "상품 수정", description = "상품 ID로 기존 상품 정보를 수정합니다.")
    @PutMapping("/{productId}")
    public RsData<ProductResDto> updateProduct(
            @PathVariable Long productId,
            @RequestBody @Valid ProductUpdateReqDto request) {
        ProductResDto response = productService.updateProduct(productId, request);
        return RsDataUtil.success("상품 수정 성공", response);
    }

    @Operation(summary = "상품 삭제", description = "상품 ID로 상품을 삭제합니다.")
    @DeleteMapping("/{productId}")
    public RsData<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return RsDataUtil.success("상품 삭제 성공", null);
    }
}
