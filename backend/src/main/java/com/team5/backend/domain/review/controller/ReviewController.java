package com.team5.backend.domain.review.controller;

import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewPatchReqDto;
import com.team5.backend.domain.review.dto.ReviewResDto;
import com.team5.backend.domain.review.dto.ReviewUpdateReqDto;
import com.team5.backend.domain.review.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "리뷰 API", description = "리뷰 관련 CRUD 및 조회 기능을 제공합니다.")
@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "리뷰 생성", description = "회원이 특정 상품에 대해 리뷰를 작성합니다.")
    @PostMapping
    public ResponseEntity<ReviewResDto> createReview(
            @RequestBody ReviewCreateReqDto request
    ) {
        ReviewResDto response = reviewService.createReview(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "전체 리뷰 조회", description = "모든 리뷰를 최신순으로 페이징 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<ReviewResDto>> getAllReviews(
            @Parameter(description = "페이징 정보") @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ReviewResDto> response = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 단건 조회", description = "리뷰 ID를 기반으로 단일 리뷰 정보를 조회합니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResDto> getReviewById(
            @Parameter(description = "리뷰 ID") @PathVariable Long id
    ) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    @Operation(summary = "리뷰 전체 수정", description = "리뷰 ID로 기존 리뷰를 전체 수정합니다.")
    @PutMapping("/{id}")
    public ResponseEntity<ReviewResDto> updateReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long id,
            @RequestBody ReviewUpdateReqDto request
    ) {
        ReviewResDto response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 부분 수정", description = "리뷰 ID로 기존 리뷰를 일부 수정합니다.")
    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResDto> patchReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long id,
            @RequestBody ReviewPatchReqDto request
    ) {
        ReviewResDto response = reviewService.patchReview(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "리뷰 삭제", description = "리뷰 ID로 특정 리뷰를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(
            @Parameter(description = "리뷰 ID") @PathVariable Long id
    ) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "상품별 리뷰 조회", description = "상품 ID로 리뷰들을 조회하며, 최신순 또는 평점순으로 정렬할 수 있습니다.")
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ReviewResDto>> getReviewsByProductId(
            @Parameter(description = "상품 ID") @PathVariable Long productId,
            @Parameter(description = "정렬 기준 (latest 또는 rate)") @RequestParam(defaultValue = "latest") String sortBy,
            @Parameter(description = "페이징 정보") @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ReviewResDto> response = reviewService.getReviewsByProductId(productId, pageable, sortBy);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "회원별 리뷰 조회", description = "회원 ID로 작성한 리뷰들을 최신순으로 조회합니다.")
    @GetMapping("/member/{memberId}")
    public ResponseEntity<Page<ReviewResDto>> getReviewsByMemberId(
            @Parameter(description = "회원 ID") @PathVariable Long memberId,
            @Parameter(description = "페이징 정보") @PageableDefault(size = 5) Pageable pageable
    ) {
        Page<ReviewResDto> response = reviewService.getReviewsByMemberId(memberId, pageable);
        return ResponseEntity.ok(response);
    }
}
