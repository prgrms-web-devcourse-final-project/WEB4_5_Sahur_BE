package com.team5.backend.domain.review.service;

import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.dto.ReviewCreateReqDto;
import com.team5.backend.domain.review.dto.ReviewPatchReqDto;
import com.team5.backend.domain.review.dto.ReviewResDto;
import com.team5.backend.domain.review.dto.ReviewUpdateReqDto;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.entity.ReviewSortField;
import com.team5.backend.domain.review.repository.ReviewRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.*;
import com.team5.backend.global.security.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final HistoryRepository historyRepository;

    /**
     * 리뷰 생성
     */
    @Transactional
    public ReviewResDto createReview(ReviewCreateReqDto request, PrincipalDetails userDetails) {
        Long memberId = userDetails.getMember().getMemberId();

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() -> new CustomException(ProductErrorCode.PRODUCT_NOT_FOUND));
        History history = historyRepository.findById(request.getHistoryId())
                .orElseThrow(() -> new CustomException(HistoryErrorCode.HISTORY_NOT_FOUND));

        Review review = Review.builder()
                .member(member)
                .product(product)
                .history(history)
                .comment(request.getComment())
                .rate(request.getRate())
                .imageUrl(request.getImageUrl()) // request.getImageUrl() → List<String>
                .createdAt(LocalDateTime.now())
                .build();


        history.setWritable(false);
        Review saved = reviewRepository.save(review);
        return ReviewResDto.fromEntity(saved);
    }

    /**
     * 전체 리뷰 목록 조회 (최신순 정렬)
     */
    @Transactional(readOnly = true)
    public Page<ReviewResDto> getAllReviews(Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findAll(sortedPageable)
                .map(ReviewResDto::fromEntity);
    }

    /**
     * 리뷰 ID로 단건 조회
     */
    @Transactional(readOnly = true)
    public ReviewResDto getReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(ReviewResDto::fromEntity)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * 리뷰 수정 (전체 필드)
     */
    @Transactional
    public ReviewResDto updateReview(Long id, ReviewUpdateReqDto request) {
        return reviewRepository.findById(id)
                .map(existing -> {
                    existing.setComment(request.getComment());
                    existing.setRate(request.getRate());
                    existing.setImageUrl(request.getImageUrl());
                    Review updated = reviewRepository.save(existing);
                    return ReviewResDto.fromEntity(updated);
                })
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * 리뷰 수정 (일부 필드)
     */
    @Transactional
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
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
    }

    /**
     * 리뷰 삭제
     */
    @Transactional
    public void deleteReview(Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new CustomException(ReviewErrorCode.REVIEW_NOT_FOUND));
        review.getHistory().setWritable(true);
        reviewRepository.deleteById(id);
    }

    /**
     * 특정 상품(productId)의 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReviewResDto> getReviewsByProductId(Long productId, Pageable pageable, ReviewSortField sortField) {
        Sort sort = sortField.toSort();
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findByProductProductId(productId, sortedPageable)
                .map(ReviewResDto::fromEntity);
    }

    /**
     * 특정 회원의 리뷰 목록 조회
     */
    @Transactional(readOnly = true)
    public Page<ReviewResDto> getReviewsByMember(PrincipalDetails userDetails, Pageable pageable) {
        Long memberId = userDetails.getMember().getMemberId();

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);

        return reviewRepository.findByMemberMemberId(memberId, sortedPageable)
                .map(ReviewResDto::fromEntity);
    }
}
