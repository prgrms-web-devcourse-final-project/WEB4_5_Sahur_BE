package com.team5.backend.domain.review.controller;

import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewResDto;
import com.team5.backend.domain.review.dto.ReviewUpdateReqDto;
import com.team5.backend.domain.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ReviewResDto createReview(@RequestBody ReviewCreateReqDto request) {
        return reviewService.createReview(request);
    }

    @GetMapping
    public List<ReviewResDto> getAllReviews() {
        return reviewService.getAllReviews();
    }

    @GetMapping("/{id}")
    public ReviewResDto getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    @PutMapping("/{id}")
    public ReviewResDto updateReview(@PathVariable Long id, @RequestBody ReviewUpdateReqDto request) {
        return reviewService.updateReview(id, request);
    }

    @PatchMapping("/{id}")
    public ReviewResDto patchReview(@PathVariable Long id, @RequestBody ReviewUpdateReqDto request) {
        return reviewService.patchReview(id, request);
    }

    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }
}
