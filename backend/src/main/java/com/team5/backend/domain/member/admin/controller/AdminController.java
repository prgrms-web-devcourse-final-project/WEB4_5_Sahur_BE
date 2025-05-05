package com.team5.backend.domain.member.admin.controller;

import com.team5.backend.domain.groupBuy.dto.GroupBuyCreateReqDto;
import com.team5.backend.domain.member.admin.dto.ProductRequestResDto;
import com.team5.backend.domain.member.admin.dto.ProductRequestUpdateReqDto;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import com.team5.backend.domain.member.admin.service.AdminService;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Tag(name = "AdminController", description = "관리자 API")
@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @Operation(summary = "상품 등록 요청 목록 조회", description = "상품 등록 요청 목록을 조회합니다.")
    @GetMapping("/productRequest/list")
    public RsData<Page<ProductRequestResDto>> getProductRequests(
            @PageableDefault(size = 5) Pageable pageable,
            @RequestParam(value = "status", required = false) ProductRequestStatus status
    ) {
        Page<ProductRequestResDto> result = adminService.getProductRequests(pageable, status);
        return RsDataUtil.success("상품 등록 요청 목록 조회 성공", result);
    }

    @Operation(summary = "공동구매 생성", description = "관리자가 상품 등록 요청을 승인한 후 관리자가 공동구매를 생성합니다.")
    @PostMapping("/groupBuy")
    public RsData<String> createGroupBuy(
            @RequestBody @Valid GroupBuyCreateReqDto request
    ) {
        adminService.createGroupBuy(request);
        return RsDataUtil.success("공동구매가 생성되었습니다.", "success");
    }

    @PatchMapping("/productRequest/{productRequestId}/status")
    @Operation(summary = "상품 등록 요청 상태 수정", description = "관리자가 상품 등록 요청의 상태를 APPROVED 또는 REJECTED로 변경합니다.")
    public RsData<String> updateProductRequestStatus(
            @Parameter(description = "상품 등록 요청 ID", required = true)
            @PathVariable Long productRequestId,
            @RequestBody @Valid ProductRequestUpdateReqDto request
    ) {
        adminService.updateProductRequestStatus(productRequestId, request.getStatus());
        return RsDataUtil.success("상품 등록 요청 상태가 수정되었습니다.", "success");
    }

    @Operation(summary = "전체 상품 목록 조회", description = "관리자가 전체 상품을 조회하고 검색합니다.")
    @GetMapping("/products")
    public RsData<Page<ProductResDto>> getAllProducts(
            @RequestParam(required = false) String search,
            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<ProductResDto> result = adminService.getAllProducts(search, pageable);
        return RsDataUtil.success("상품 목록 조회 성공", result);
    }

    @Operation(summary = "상품 상세 조회", description = "관리자가 상품의 상세 정보를 조회합니다.")
    @GetMapping("/products/{productId}")
    public RsData<ProductResDto> getProductDetail(
            @Parameter(description = "상품 ID", required = true)
            @PathVariable Long productId
    ) {
        ProductResDto result = adminService.getProductDetail(productId);
        return RsDataUtil.success("상품 상세 조회 성공", result);
    }

}
