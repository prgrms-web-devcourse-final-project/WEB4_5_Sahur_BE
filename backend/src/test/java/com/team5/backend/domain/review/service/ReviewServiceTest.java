//package com.team5.backend.domain.review.service;
//
//import com.team5.backend.domain.history.entity.History;
//import com.team5.backend.domain.history.repository.HistoryRepository;
//import com.team5.backend.domain.member.member.entity.Member;
//import com.team5.backend.domain.member.member.repository.MemberRepository;
//import com.team5.backend.domain.product.entity.Product;
//import com.team5.backend.domain.product.repository.ProductRepository;
//import com.team5.backend.domain.review.dto.*;
//import com.team5.backend.domain.review.entity.Review;
//import com.team5.backend.domain.review.entity.ReviewSortField;
//import com.team5.backend.domain.review.repository.ReviewRepository;
//import com.team5.backend.global.security.PrincipalDetails;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.*;
//import org.springframework.data.domain.*;
//
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class ReviewServiceTest {
//
//    @Mock private ReviewRepository reviewRepository;
//    @Mock private MemberRepository memberRepository;
//    @Mock private ProductRepository productRepository;
//    @Mock private HistoryRepository historyRepository;
//
//    @InjectMocks
//    private ReviewService reviewService;
//
//    private Member member;
//    private Product product;
//    private Review review;
//    private History history;
//    private PrincipalDetails userDetails;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//
//        member = Member.builder().memberId(1L).email("test@team5.com").nickname("테스터").build();
//        product = Product.builder().productId(1L).title("테스트상품").build();
//        history = History.builder().historyId(1L).member(member).product(product).build();
//        userDetails = new PrincipalDetails(member, Map.of());
//
//        review = Review.builder()
//                .reviewId(1L)
//                .member(member)
//                .product(product)
//                .history(history)
//                .comment("좋아요")
//                .rate(5)
//                .imageUrl("url")
//                .createdAt(LocalDateTime.now())
//                .build();
//    }
//
//    @Test
//    @DisplayName("리뷰 생성")
//    void createReview() {
//        ReviewCreateReqDto dto = ReviewCreateReqDto.builder()
//                .productId(1L)
//                .historyId(1L)
//                .comment("좋아요")
//                .rate(5)
//                .imageUrl("url")
//                .build();
//
//        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
//        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
//        when(historyRepository.findById(1L)).thenReturn(Optional.of(history));
//        when(reviewRepository.save(any())).thenReturn(review);
//
//        ReviewResDto result = reviewService.createReview(dto, userDetails);
//
//        assertEquals("좋아요", result.getComment());
//        verify(reviewRepository).save(any());
//    }
//
//    @Test
//    @DisplayName("전체 리뷰 조회")
//    void getAllReviews() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Review> page = new PageImpl<>(List.of(review));
//
//        when(reviewRepository.findAll(any(Pageable.class))).thenReturn(page);
//
//        Page<ReviewResDto> result = reviewService.getAllReviews(pageable);
//
//        assertEquals(1, result.getTotalElements());
//    }
//
//    @Test
//    @DisplayName("리뷰 ID로 조회")
//    void getReviewById() {
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//        ReviewResDto result = reviewService.getReviewById(1L);
//
//        assertEquals("좋아요", result.getComment());
//    }
//
//    @Test
//    @DisplayName("전체 필드 리뷰 수정")
//    void updateReview() {
//        ReviewUpdateReqDto dto = ReviewUpdateReqDto.builder()
//                .comment("최고")
//                .rate(4)
//                .imageUrl("newUrl")
//                .build();
//
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//        when(reviewRepository.save(any())).thenReturn(review);
//
//        ReviewResDto result = reviewService.updateReview(1L, dto);
//
//        assertEquals("최고", result.getComment());
//    }
//
//    @Test
//    @DisplayName("일부 필드 리뷰 수정 - patch")
//    void patchReview() {
//        ReviewPatchReqDto dto = ReviewPatchReqDto.builder()
//                .rate(3)
//                .build();
//
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//        when(reviewRepository.save(any())).thenReturn(review);
//
//        ReviewResDto result = reviewService.patchReview(1L, dto);
//
//        assertEquals(3, result.getRate());
//        assertEquals("좋아요", result.getComment()); // 기존 유지
//    }
//
//    @Test
//    @DisplayName("리뷰 삭제")
//    void deleteReview() {
//        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
//        reviewService.deleteReview(1L);
//        verify(reviewRepository).deleteById(1L);
//    }
//
//    @Test
//    @DisplayName("상품 ID로 리뷰 조회 - 최신순 정렬")
//    void getReviewsByProductId_latestSorted() {
//        Review r1 = Review.builder().reviewId(1L).product(product).member(member).rate(3)
//                .comment("첫째").createdAt(LocalDateTime.now().minusDays(3)).history(history).build();
//        Review r2 = Review.builder().reviewId(2L).product(product).member(member).rate(4)
//                .comment("둘째").createdAt(LocalDateTime.now().minusDays(2)).history(history).build();
//        Review r3 = Review.builder().reviewId(3L).product(product).member(member).rate(5)
//                .comment("셋째").createdAt(LocalDateTime.now().minusDays(1)).history(history).build();
//
//        Page<Review> page = new PageImpl<>(List.of(r3, r2, r1));
//        Pageable pageable = PageRequest.of(0, 5);
//
//        when(reviewRepository.findByProductProductId(eq(1L), any(Pageable.class))).thenReturn(page);
//
//        Page<ReviewResDto> result = reviewService.getReviewsByProductId(1L, pageable, ReviewSortField.LATEST);
//
//        assertEquals("셋째", result.getContent().get(0).getComment());
//        assertEquals("둘째", result.getContent().get(1).getComment());
//        assertEquals("첫째", result.getContent().get(2).getComment());
//    }
//
//    @Test
//    @DisplayName("상품 ID로 리뷰 조회 - 평점순 정렬")
//    void getReviewsByProductId_rateSorted() {
//        Review r1 = Review.builder().reviewId(1L).product(product).member(member).rate(2)
//                .comment("낮음").history(history).build();
//        Review r2 = Review.builder().reviewId(2L).product(product).member(member).rate(4)
//                .comment("중간").history(history).build();
//        Review r3 = Review.builder().reviewId(3L).product(product).member(member).rate(5)
//                .comment("높음").history(history).build();
//
//        Page<Review> page = new PageImpl<>(List.of(r3, r2, r1));
//        Pageable pageable = PageRequest.of(0, 5);
//
//        when(reviewRepository.findByProductProductId(eq(1L), any(Pageable.class))).thenReturn(page);
//
//        Page<ReviewResDto> result = reviewService.getReviewsByProductId(1L, pageable, ReviewSortField.RATE);
//
//        assertEquals(5, result.getContent().get(0).getRate());
//        assertEquals("높음", result.getContent().get(0).getComment());
//    }
//
//    @Test
//    @DisplayName("회원 기반 리뷰 조회")
//    void getReviewsByMember() {
//        Pageable pageable = PageRequest.of(0, 5);
//        Page<Review> page = new PageImpl<>(List.of(review));
//
//        when(reviewRepository.findByMemberMemberId(eq(1L), any(Pageable.class))).thenReturn(page);
//
//        Page<ReviewResDto> result = reviewService.getReviewsByMember(userDetails, pageable);
//
//        assertEquals(1, result.getTotalElements());
//        verify(reviewRepository).findByMemberMemberId(eq(1L), any(Pageable.class));
//    }
//}
