package com.team5.backend.domain.review.dto;

import com.team5.backend.domain.member.member.dto.MemberDto;
import com.team5.backend.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResDto {
    private Long reviewId;
    private MemberDto member; // 변경
    private Long productId;
    private Long historyId;
    private String comment;
    private Integer rate;
    private LocalDateTime createdAt;
    private List<String> imageUrl;

    public static ReviewResDto fromEntity(Review review) {
        return ReviewResDto.builder()
                .reviewId(review.getReviewId())
                .member(MemberDto.fromEntity(review.getMember())) // 변경
                .productId(review.getProduct().getProductId())
                .historyId(review.getHistory().getHistoryId())
                .comment(review.getComment())
                .rate(review.getRate())
                .createdAt(review.getCreatedAt())
                .imageUrl(review.getImageUrl())
                .build();
    }
}


