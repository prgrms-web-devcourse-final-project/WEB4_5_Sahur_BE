package com.team5.backend.domain.review.service;

import com.team5.backend.domain.member.entity.Member;
import com.team5.backend.domain.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewResDto;
import com.team5.backend.domain.review.dto.ReviewUpdateReqDto;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
        return toResponse(saved);
    }

    public List<ReviewResDto> getAllReviews() {
        return reviewRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public Optional<ReviewResDto> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(this::toResponse);
    }

    public ReviewResDto updateReview(Long id, ReviewUpdateReqDto request) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setComment(request.getComment());
                    existing.setRate(request.getRate());
                    existing.setImageUrl(request.getImageUrl());
                    Review updated = reviewRepository.save(existing);
                    return toResponse(updated);
                })
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    public ReviewResDto patchReview(Long id, ReviewUpdateReqDto request) {
        return reviewRepository.findById(id)
                .map(existingReview -> {
                    // 제공된 값만 업데이트 (null 값은 업데이트하지 않음)
                    if (request.getComment() != null) {
                        existingReview.setComment(request.getComment());
                    }
                    if (request.getRate() != null) {
                        existingReview.setRate(request.getRate());
                    }
                    if (request.getImageUrl() != null) {
                        existingReview.setImageUrl(request.getImageUrl());
                    }

                    // 수정된 엔티티 저장
                    Review updatedReview = reviewRepository.save(existingReview);
                    return toResponse(updatedReview);
                })
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }


    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private ReviewResDto toResponse(Review review) {
        return ReviewResDto.builder()
                .reviewId(review.getReviewId())
                .memberId(review.getMember().getMemberId())
                .productId(review.getProduct().getProductId())
                .comment(review.getComment())
                .rate(review.getRate())
                .createdAt(review.getCreatedAt())
                .imageUrl(review.getImageUrl())
                .build();
    }
}
