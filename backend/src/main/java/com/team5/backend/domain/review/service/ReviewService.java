package com.team5.backend.domain.review.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewResDto;
import com.team5.backend.domain.review.dto.ReviewUpdateReqDto;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;

    public ReviewResDto createReview(ReviewCreateReqDto request) {
        Member member = memberRepository.findById(request.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Review review = Review.builder()
                .member(member)
                .product(product)
                .comment(request.getComment())
                .rate(request.getRate())
                .imageUrl(request.getImageUrl())
                .createdAt(LocalDateTime.now())
                .build();

        Review saved = reviewRepository.save(review);
        return ReviewResDto.fromEntity(saved); // ✅
    }

    public Page<ReviewResDto> getAllReviews(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findAll(sortedPageable)
                .map(ReviewResDto::fromEntity); // ✅
    }

    public Optional<ReviewResDto> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewResDto::fromEntity); // ✅
    }

    public ReviewResDto updateReview(Long id, ReviewUpdateReqDto request) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setComment(request.getComment());
                    existing.setRate(request.getRate());
                    existing.setImageUrl(request.getImageUrl());
                    Review updated = reviewRepository.save(existing);
                    return ReviewResDto.fromEntity(updated); // ✅
                })
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    public ReviewResDto patchReview(Long id, ReviewUpdateReqDto request) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    if (request.getComment() != null) {
                        existingReview.setComment(request.getComment());
                    }
                    if (request.getRate() != null) {
                        existingReview.setRate(request.getRate());
                    }
                    if (request.getImageUrl() != null) {
                        existingReview.setImageUrl(request.getImageUrl());
                    }

                    Review updatedReview = reviewRepository.save(existingReview);
                    return ReviewResDto.fromEntity(updatedReview); // ✅
                })
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
}
