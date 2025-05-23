package com.team5.backend.domain.member.productrequest.controller;

import com.team5.backend.domain.member.productrequest.dto.ProductRequestCreateReqDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestResDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestUpdateReqDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.service.ProductRequestService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.annotation.CheckAdmin;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "ProductRequest", description = "상품 등록 요청 API")
@RestController
@RequestMapping("/api/v1/productRequests")
@RequiredArgsConstructor
public class ProductRequestController {

    private final ProductRequestService productRequestService;

    @Operation(summary = "상품 등록 요청 생성", description = "사용자가 새로운 상품 등록 요청을 생성합니다.")
    @PostMapping
    public RsData<ProductRequestResDto> createRequest(
            @RequestBody @Valid ProductRequestCreateReqDto request,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        ProductRequestResDto response = productRequestService.createRequest(request, userDetails);
        return RsDataUtil.success("상품 등록 요청 생성 완료", response);
    }

    @Operation(summary = "전체 또는 상태별 상품 요청 조회 (관리자)", description = "전체 상품 요청 또는 특정 상태별 요청을 페이징 조회합니다.")
    @GetMapping("/list")
    public RsData<Page<ProductRequestResDto>> getAllRequests(
            @Parameter(description = "요청 상태 (없으면 전체 조회)", example = "WAITING")
            @RequestParam(required = false) ProductRequestStatus status,
            @Parameter(description = "페이지 정보")
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ProductRequestResDto> responses = productRequestService.getAllRequests(status, sortedPageable);
        return RsDataUtil.success("상품 요청 조회 완료", responses);
    }


    @Operation(summary = "회원 본인 상품 요청 조회", description = "로그인한 사용자가 본인이 등록한 요청 목록을 조회합니다.")
    @GetMapping("/me")
    public RsData<Page<ProductRequestResDto>> getMyRequests(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @Parameter(description = "페이지 정보")
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ProductRequestResDto> responses = productRequestService.getRequestsByMember(userDetails, sortedPageable);
        return RsDataUtil.success("내 상품 요청 조회 완료", responses);
    }

    @Operation(summary = "상품 요청 단건 조회", description = "상품 등록 요청 ID로 단건 상세 조회합니다.")
    @GetMapping("/{productRequestId}")
    public RsData<ProductRequestResDto> getRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId
    ) {
        ProductRequestResDto response = productRequestService.getRequest(productRequestId);
        return RsDataUtil.success("상품 요청 단건 조회 완료", response);
    }

    @Operation(summary = "회원 본인 상품 요청 수정", description = "로그인한 사용자가 본인의 요청을 수정합니다.")
    @PatchMapping("/{productRequestId}")
    public RsData<ProductRequestResDto> updateRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId,
            @RequestBody @Valid ProductRequestUpdateReqDto request,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        ProductRequestResDto response = productRequestService.updateRequest(productRequestId, request, userDetails);
        return RsDataUtil.success("상품 요청 수정 완료", response);
    }

    @Operation(summary = "상품 요청 승인/거절 (관리자)", description = "상품 요청을 승인하거나 거절 처리합니다.")
    @PatchMapping("/{productRequestId}/{confirm}")
    public RsData<ProductRequestResDto> confirmRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId,
            @Parameter(description = "처리 타입 (approve / reject)", example = "approve") @PathVariable String confirm
    ) {
        ProductRequestResDto response = productRequestService.updateStatus(productRequestId, confirm);
        return RsDataUtil.success("상품 요청 처리 완료", response);
    }

    @Operation(summary = "회원 본인 상품 요청 취소", description = "로그인한 사용자가 자신의 요청을 삭제합니다.")
    @DeleteMapping("/{productRequestId}")
    public RsData<Empty> deleteRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) {
        productRequestService.deleteRequest(productRequestId, userDetails);
        return RsDataUtil.success("상품 요청이 삭제되었습니다.");
    }
}
