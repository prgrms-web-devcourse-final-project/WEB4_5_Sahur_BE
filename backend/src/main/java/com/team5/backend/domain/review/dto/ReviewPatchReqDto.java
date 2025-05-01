package com.team5.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewPatchReqDto {
    private String comment;
    private Integer rate;
    private String imageUrl;
}
