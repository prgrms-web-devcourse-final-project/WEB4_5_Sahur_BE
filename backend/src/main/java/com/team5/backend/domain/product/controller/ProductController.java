package com.team5.backend.domain.product.controller;

import com.team5.backend.domain.product.dto.ProductCreateReqDto;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.dto.ProductUpdateReqDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.service.ProductService;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Product", description = "상품 관련 API")
@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 생성", description = "새로운 상품을 등록합니다.")
    @CheckAdmin
    @PostMapping
    public RsData<ProductResDto> createProduct(@RequestPart(value = "request", required = false) @Valid ProductCreateReqDto request,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {
        ProductResDto response = productService.createProduct(request, imageFiles);
        return RsDataUtil.success("상품 생성 성공", response);
    }

    @Operation(summary = "상품 목록 조회", description = "카테고리 또는 키워드로 상품을 검색하고, 페이징 처리된 결과를 반환합니다.")
    @GetMapping
    public RsData<Page<ProductResDto>> getAllProducts(
            @Parameter(description = "카테고리명") @RequestParam(required = false) String category,
            @Parameter(description = "검색 키워드") @RequestParam(required = false) String keyword,
            @ParameterObject @Parameter(description = "페이지 정보") @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<Product> page = productService.getAllProducts(category, keyword, pageable);
        Page<ProductResDto> response = page.map(ProductResDto::fromEntity);
        return RsDataUtil.success("상품 목록 조회 성공", response);
    }

    @Operation(summary = "상품 단건 조회", description = "상품 ID로 특정 상품을 조회합니다.")
    @GetMapping("/{productId}")
    public RsData<ProductResDto> getProductById(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        ProductResDto response = productService.getProductById(productId);
        return RsDataUtil.success("상품 조회 성공", response);
    }

    @Operation(summary = "상품 수정", description = "상품 ID로 기존 상품 정보를 수정합니다.")
    @CheckAdmin
    @PatchMapping("/{productId}")
    public RsData<ProductResDto> updateProduct(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @RequestPart(value = "request", required = false) @Valid ProductUpdateReqDto request,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles) throws IOException {
        ProductResDto response = productService.updateProduct(productId, request, imageFiles);
        return RsDataUtil.success("상품 수정 성공", response);
    }

    @Operation(summary = "상품 삭제", description = "상품 ID로 상품을 삭제합니다.")
    @CheckAdmin
    @DeleteMapping("/{productId}")
    public RsData<Empty> deleteProduct(
            @Parameter(description = "상품 ID") @PathVariable Long productId) throws IOException {
        productService.deleteProduct(productId);
        return RsDataUtil.success("상품 삭제 성공");
    }

    @Operation(summary = "상품 관심 등록 수 조회", description = "상품 ID로 상품의 관심 등록 수를 조회합니다.")
    @GetMapping("/{productId}/dibs/count")
    public RsData<Long> getDibCount(
            @Parameter(description = "상품 ID") @PathVariable Long productId) {
        Long dibCount = productService.getDibCount(productId);
        return RsDataUtil.success("관심 등록 수 조회 성공", dibCount);
    }
}
