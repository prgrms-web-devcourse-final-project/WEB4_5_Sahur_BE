package com.team5.backend.domain.review.dto;

import lombok.Getter;

@Getter
public class ReviewUpdateReqDto {
    private String comment;
    private Integer rate;
    private String imageUrl;
}
