package com.team5.backend.domain.review.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewUpdateReqDto {
    private String comment;
    private Integer rate;
    private String imageUrl;
}
