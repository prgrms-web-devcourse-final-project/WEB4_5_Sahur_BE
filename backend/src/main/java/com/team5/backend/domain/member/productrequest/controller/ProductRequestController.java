package com.team5.backend.domain.member.productrequest.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.team5.backend.domain.member.productrequest.dto.ProductRequestCreateReqDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestDetailResDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestListResDto;
import com.team5.backend.domain.member.productrequest.dto.ProductRequestUpdateReqDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import com.team5.backend.domain.member.productrequest.service.ProductRequestService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

@Tag(name = "ProductRequest", description = "상품 등록 요청 API")
@RestController
@RequestMapping("/api/v1/productRequests")
@RequiredArgsConstructor
public class ProductRequestController {

    private final ProductRequestService productRequestService;

    @Operation(summary = "상품 등록 요청 생성", description = "사용자가 새로운 상품 등록 요청을 생성합니다.")
    @PostMapping
    public RsData<ProductRequestDetailResDto> createRequest(
            @RequestPart(value = "request", required = false) @Valid ProductRequestCreateReqDto request,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) throws IOException {
        ProductRequestDetailResDto response = productRequestService.createRequest(request, imageFiles, userDetails);
        return RsDataUtil.success("상품 등록 요청 생성 완료", response);
    }

    @Operation(summary = "전체 또는 상태별 상품 요청 조회 (관리자)", description = "전체 상품 요청 또는 특정 상태별 요청을 페이징 조회합니다.")
    @GetMapping("/list")
    public RsData<Page<ProductRequestListResDto>> getAllRequests(
            @Parameter(description = "요청 상태 (없으면 전체 조회)", example = "WAITING")
            @RequestParam(value = "status", required = false) ProductRequestStatus status,
            @Parameter(description = "페이지 정보")
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ProductRequestListResDto> responses;
        if (status == null) {
            responses = productRequestService.getAllRequests(sortedPageable);
        } else {
            responses = productRequestService.getAllRequestsByStatus(status, sortedPageable);
        }
        return RsDataUtil.success("상품 요청 조회 완료", responses);
    }


    @Operation(summary = "회원 본인 상품 요청 조회", description = "로그인한 사용자가 본인이 등록한 요청 목록을 조회합니다.")
    @GetMapping("/me")
    public RsData<Page<ProductRequestListResDto>> getMyRequests(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @Parameter(description = "페이지 정보")
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Pageable sortedPageable = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<ProductRequestListResDto> responses = productRequestService.getRequestsByMember(userDetails, sortedPageable);
        return RsDataUtil.success("내 상품 요청 조회 완료", responses);
    }

    @Operation(summary = "상품 요청 단건 조회", description = "상품 등록 요청 ID로 단건 상세 조회합니다.")
    @GetMapping("/{productRequestId}")
    public RsData<ProductRequestDetailResDto> getRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId
    ) {
        ProductRequestDetailResDto response = productRequestService.getRequest(productRequestId);
        return RsDataUtil.success("상품 요청 단건 조회 완료", response);
    }

    @Operation(summary = "회원 본인 상품 요청 수정", description = "로그인한 사용자가 본인의 요청을 수정합니다.")
    @PatchMapping("/{productRequestId}")
    public RsData<ProductRequestDetailResDto> updateRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId,
            @RequestPart(value = "request", required = false) @Valid ProductRequestUpdateReqDto request,
            @RequestPart(value = "images", required = false) List<MultipartFile> imageFiles,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) throws IOException {
        ProductRequestDetailResDto response = productRequestService.updateRequest(productRequestId, request, imageFiles, userDetails);
        return RsDataUtil.success("상품 요청 수정 완료", response);
    }

    @Operation(summary = "상품 요청 승인/거절 (관리자)", description = "상품 요청을 승인하거나 거절 처리합니다.")
    @PatchMapping("/{productRequestId}/{confirm}")
    public RsData<ProductRequestDetailResDto> confirmRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId,
            @Parameter(description = "처리 타입 (approve / reject)", example = "approve") @PathVariable String confirm,
            @RequestBody(required = false) String message
    ) {
        ProductRequestDetailResDto response = productRequestService.updateStatus(productRequestId, confirm, message);
        return RsDataUtil.success("상품 요청 처리 완료", response);
    }

    @Operation(summary = "회원 본인 상품 요청 취소", description = "로그인한 사용자가 자신의 요청을 삭제합니다.")
    @DeleteMapping("/{productRequestId}")
    public RsData<Empty> deleteRequest(
            @Parameter(description = "상품 요청 ID") @PathVariable Long productRequestId,
            @AuthenticationPrincipal PrincipalDetails userDetails
    ) throws IOException {
        productRequestService.deleteRequest(productRequestId, userDetails);
        return RsDataUtil.success("상품 요청이 삭제되었습니다.");
    }
}
