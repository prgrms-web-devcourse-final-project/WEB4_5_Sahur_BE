package com.team5.backend.domain.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ReviewUpdateReqDto {
    @NotNull
    private String comment;
    @NotNull
    private Integer rate;
    @NotNull
    private String imageUrl;
}
