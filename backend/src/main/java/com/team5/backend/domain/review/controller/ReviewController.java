package com.team5.backend.domain.review.controller;

import com.team5.backend.domain.review.dto.*;
import com.team5.backend.domain.review.entity.ReviewSortField;
import com.team5.backend.domain.review.service.ReviewService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 API", description = "리뷰 관련 CRUD 및 조회 기능을 제공합니다.")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "회원이 특정 상품에 대해 리뷰를 작성합니다.")
    @PostMapping
    public RsData<ReviewResDto> createReview(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @RequestBody @Valid ReviewCreateReqDto request
    ) {
        ReviewResDto response = reviewService.createReview(request, userDetails);
        return RsDataUtil.success("리뷰 생성 성공", response);
    }

    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 최신순으로 페이징 조회합니다.")
    @GetMapping
    public RsData<Page<ReviewResDto>> getAllReviews(
            @Parameter(description = "페이징 정보") @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ReviewResDto> response = reviewService.getAllReviews(pageable);
        return RsDataUtil.success("리뷰 목록 조회 성공", response);
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID를 기반으로 단일 리뷰 정보를 조회합니다.")
    @GetMapping("/{id}")
    public RsData<ReviewResDto> getReviewById(@PathVariable Long id) {
        ReviewResDto dto = reviewService.getReviewById(id);
        return RsDataUtil.success("리뷰 조회 성공", dto);
    }

    @Operation(summary = "리뷰 전체 수정", description = "리뷰 ID로 기존 리뷰를 전체 수정합니다.")
    @PutMapping("/{id}")
    public RsData<ReviewResDto> updateReview(
            @PathVariable Long id,
            @RequestBody @Valid ReviewUpdateReqDto request
    ) {
        ReviewResDto response = reviewService.updateReview(id, request);
        return RsDataUtil.success("리뷰 수정 성공", response);
    }

    @Operation(summary = "리뷰 부분 수정", description = "리뷰 ID로 기존 리뷰를 일부 수정합니다.")
    @PatchMapping("/{id}")
    public RsData<ReviewResDto> patchReview(
            @PathVariable Long id,
            @RequestBody ReviewPatchReqDto request
    ) {
        ReviewResDto response = reviewService.patchReview(id, request);
        return RsDataUtil.success("리뷰 수정 성공", response);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 ID로 특정 리뷰를 삭제합니다.")
    @DeleteMapping("/{id}")
    public RsData<Empty> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return RsDataUtil.success("리뷰 삭제 성공");
    }

    @Operation(summary = "상품별 리뷰 조회", description = "상품 ID로 리뷰들을 조회하며, 최신순 또는 평점순으로 정렬할 수 있습니다.")
    @GetMapping("/product/{productId}/list")
    public RsData<Page<ReviewResDto>> getReviewsByProductId(
            @PathVariable Long productId,
            @RequestParam(defaultValue = "LATEST") ReviewSortField sortBy,
            @PageableDefault(size = 3) Pageable pageable
    ) {
        Page<ReviewResDto> response = reviewService.getReviewsByProductId(productId, pageable, sortBy);
        return RsDataUtil.success("상품 리뷰 조회 성공", response);
    }

    @Operation(summary = "내 리뷰 조회", description = "현재 로그인한 회원이 작성한 리뷰를 최신순으로 조회합니다.")
    @GetMapping("/member/list")
    public RsData<Page<ReviewResDto>> getMyReviews(
            @AuthenticationPrincipal PrincipalDetails userDetails,
            @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ReviewResDto> response = reviewService.getReviewsByMember(userDetails, pageable);
        return RsDataUtil.success("내 리뷰 조회 성공", response);
    }
}
