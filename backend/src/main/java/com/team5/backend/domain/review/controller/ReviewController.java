package com.team5.backend.domain.review.controller;

import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewResDto;
import com.team5.backend.domain.review.dto.ReviewUpdateReqDto;
import com.team5.backend.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<ReviewResDto> createReview(@RequestBody ReviewCreateReqDto request) {
        ReviewResDto response = reviewService.createReview(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<Page<ReviewResDto>> getAllReviews(Pageable pageable) {
        Page<ReviewResDto> response = reviewService.getAllReviews(pageable);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReviewResDto> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewResDto> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewUpdateReqDto request) {
        ReviewResDto response = reviewService.updateReview(id, request);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ReviewResDto> patchReview(
            @PathVariable Long id,
            @RequestBody ReviewUpdateReqDto request) {
        ReviewResDto response = reviewService.patchReview(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
