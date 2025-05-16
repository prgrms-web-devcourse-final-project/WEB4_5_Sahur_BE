package com.team5.backend.domain.review.dto;

import com.team5.backend.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResDto {
    private Long reviewId;
    private Long memberId;
    private Long productId;
    private Long historyId;
    private String comment;
    private Integer rate;
    private LocalDateTime createdAt;
    private List<String> imageUrl; // 🔄 수정

    public static ReviewResDto fromEntity(Review review) {
        return ReviewResDto.builder()
                .reviewId(review.getReviewId())
                .memberId(review.getMember().getMemberId())
                .productId(review.getProduct().getProductId())
                .historyId(review.getHistory().getHistoryId())
                .comment(review.getComment())
                .rate(review.getRate())
                .createdAt(review.getCreatedAt())
                .imageUrl(review.getImageUrl()) // List<String> 그대로 사용
                .build();
    }
}

