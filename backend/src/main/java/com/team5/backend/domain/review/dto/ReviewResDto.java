package com.team5.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReviewResDto {
    private Long reviewId;
    private Long memberId;
    private Long productId;
    private String comment;
    private Integer rate;
    private LocalDateTime createdAt;
    private String imageUrl;
}
