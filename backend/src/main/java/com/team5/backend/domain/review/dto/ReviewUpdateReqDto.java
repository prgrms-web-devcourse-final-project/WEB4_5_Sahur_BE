package com.team5.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateReqDto {
    private String comment;
    private Integer rate;
    private String imageUrl;
}
