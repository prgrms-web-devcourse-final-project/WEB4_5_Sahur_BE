package com.team5.backend.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewCreateReqDto {
    private Long memberId;
    private Long productId;
    private String comment;
    private Integer rate;
    private String imageUrl;
}
