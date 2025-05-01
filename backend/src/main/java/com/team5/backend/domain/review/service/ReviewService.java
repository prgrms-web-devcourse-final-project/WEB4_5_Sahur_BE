package com.team5.backend.domain.review.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewPatchReqDto;
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

    /**
     * 리뷰 생성
     */
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
        return ReviewResDto.fromEntity(saved);
    }

    /**
     * 전체 리뷰 목록 조회 (최신순 정렬)
     */
    public Page<ReviewResDto> getAllReviews(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findAll(sortedPageable)
                .map(ReviewResDto::fromEntity);
    }

    /**
     * 리뷰 ID로 단건 조회
     */
    public Optional<ReviewResDto> getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewResDto::fromEntity);
    }

    /**
     * 리뷰 수정 (전체 필드)
     */
    public ReviewResDto updateReview(Long id, ReviewUpdateReqDto request) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setComment(request.getComment());
                    existing.setRate(request.getRate());
                    existing.setImageUrl(request.getImageUrl());
                    Review updated = reviewRepository.save(existing);
                    return ReviewResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    /**
     * 리뷰 수정 (일부 필드)
     */
    public ReviewResDto patchReview(Long id, ReviewPatchReqDto request) {
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
                    return ReviewResDto.fromEntity(updatedReview);
                })
                .orElseThrow(() -> new RuntimeException("Review not found with id " + id));
    }

    /**
     * 리뷰 삭제
     */
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    /**
     * 특정 상품(productId)의 리뷰 목록 조회
     * @param productId 상품 ID
     * @param pageable 페이징 및 정렬 정보
     * @param sortBy "latest" 또는 "rate"
     */
    public Page<ReviewResDto> getReviewsByProductId(Long productId, Pageable pageable, String sortBy) {
        Sort sort;
        if ("rate".equalsIgnoreCase(sortBy)) {
            sort = Sort.by(Sort.Order.desc("rate")); // 평점순
        } else {
            sort = Sort.by(Sort.Order.desc("createdAt")); // 기본은 최신순
        }

        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findByProductId(productId, sortedPageable)
                .map(ReviewResDto::fromEntity);
    }

    /**
     * 특정 회원(memberId)이 작성한 리뷰 목록 조회
     * @param memberId 회원 ID
     * @param pageable 페이징 정보 (기본 정렬: 최신순)
     */
    public Page<ReviewResDto> getReviewsByMemberId(Long memberId, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findByMemberId(memberId, sortedPageable)
                .map(ReviewResDto::fromEntity);
    }
}
