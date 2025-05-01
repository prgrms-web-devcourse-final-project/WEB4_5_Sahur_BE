package com.team5.backend.domain.review.service;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.domain.product.repository.ProductRepository;
import com.team5.backend.domain.review.dto.*;
import com.team5.backend.domain.review.entity.Review;
import com.team5.backend.domain.review.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private MemberRepository memberRepository;
    @Mock private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Member member;
    private Product product;
    private Review review;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        member = Member.builder().memberId(1L).email("test@team5.com").nickname("테스터").build();
        product = Product.builder().productId(1L).title("테스트상품").build();
        review = Review.builder()
                .reviewId(1L)
                .member(member)
                .product(product)
                .comment("좋아요")
                .rate(5)
                .imageUrl("url")
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("리뷰 생성")
    void createReview() {
        ReviewCreateReqDto dto = ReviewCreateReqDto.builder()
                .memberId(1L)
                .productId(1L)
                .comment("좋아요")
                .rate(5)
                .imageUrl("url")
                .build();

        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResDto result = reviewService.createReview(dto);

        assertEquals("좋아요", result.getComment());
        verify(reviewRepository).save(any());
    }

    @Test
    @DisplayName("전체 리뷰 조회")
    void getAllReviews() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> page = new PageImpl<>(List.of(review));

        when(reviewRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ReviewResDto> result = reviewService.getAllReviews(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    @DisplayName("리뷰 ID로 조회")
    void getReviewById() {
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        Optional<ReviewResDto> result = reviewService.getReviewById(1L);

        assertTrue(result.isPresent());
        assertEquals("좋아요", result.get().getComment());
    }

    @Test
    @DisplayName("전체 필드 리뷰 수정")
    void updateReview() {
        ReviewUpdateReqDto dto = ReviewUpdateReqDto.builder()
                .comment("최고")
                .rate(4)
                .imageUrl("newUrl")
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResDto result = reviewService.updateReview(1L, dto);

        assertEquals("최고", result.getComment());
    }

    @Test
    @DisplayName("일부 필드 리뷰 수정 - patch")
    void patchReview() {
        ReviewPatchReqDto dto = ReviewPatchReqDto.builder()
                .rate(3)
                .build();

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any())).thenReturn(review);

        ReviewResDto result = reviewService.patchReview(1L, dto);

        assertEquals(3, result.getRate());
        assertEquals("좋아요", result.getComment()); // 유지
    }

    @Test
    @DisplayName("리뷰 삭제")
    void deleteReview() {
        reviewService.deleteReview(1L);
        verify(reviewRepository).deleteById(1L);
    }

    @Test
    @DisplayName("상품 ID로 리뷰 조회 - 최신순 정렬 확인")
    void getReviewsByProductId_latestSorted() {
        Review r1 = Review.builder().reviewId(1L).product(product).member(member).rate(3).comment("첫째")
                .createdAt(LocalDateTime.now().minusDays(3)).build();
        Review r2 = Review.builder().reviewId(2L).product(product).member(member).rate(4).comment("둘째")
                .createdAt(LocalDateTime.now().minusDays(2)).build();
        Review r3 = Review.builder().reviewId(3L).product(product).member(member).rate(5).comment("셋째")
                .createdAt(LocalDateTime.now().minusDays(1)).build();

        Page<Review> reviewPage = new PageImpl<>(List.of(r3, r2, r1)); // 최신순으로 정렬된 결과
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("createdAt")));

        when(reviewRepository.findByProductId(eq(1L), any(Pageable.class))).thenReturn(reviewPage);

        Page<ReviewResDto> result = reviewService.getReviewsByProductId(1L, pageable, "latest");

        assertEquals("셋째", result.getContent().get(0).getComment());
        assertEquals("둘째", result.getContent().get(1).getComment());
        assertEquals("첫째", result.getContent().get(2).getComment());
    }

    @Test
    @DisplayName("상품 ID로 리뷰 조회 - 평점순 정렬 확인")
    void getReviewsByProductId_rateSorted() {
        Review r1 = Review.builder().reviewId(1L).product(product).member(member).rate(2).comment("낮음")
                .createdAt(LocalDateTime.now()).build();
        Review r2 = Review.builder().reviewId(2L).product(product).member(member).rate(4).comment("중간")
                .createdAt(LocalDateTime.now()).build();
        Review r3 = Review.builder().reviewId(3L).product(product).member(member).rate(5).comment("높음")
                .createdAt(LocalDateTime.now()).build();

        Page<Review> reviewPage = new PageImpl<>(List.of(r3, r2, r1)); // 평점순으로 정렬된 결과
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Order.desc("rate")));

        when(reviewRepository.findByProductId(eq(1L), any(Pageable.class))).thenReturn(reviewPage);

        Page<ReviewResDto> result = reviewService.getReviewsByProductId(1L, pageable, "rate");

        assertEquals(5, result.getContent().get(0).getRate());
        assertEquals(4, result.getContent().get(1).getRate());
        assertEquals(2, result.getContent().get(2).getRate());
    }

    @Test
    @DisplayName("회원 ID로 리뷰 조회")
    void getReviewsByMemberId() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Review> page = new PageImpl<>(List.of(review));
        when(reviewRepository.findByMemberId(eq(1L), any(Pageable.class))).thenReturn(page);

        Page<ReviewResDto> result = reviewService.getReviewsByMemberId(1L, pageable);
        assertEquals(1, result.getTotalElements());
    }
}
