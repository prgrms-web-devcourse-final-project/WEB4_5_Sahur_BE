package com.team5.backend.domain.review.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewUpdateReqDto {
    @NotNull
    private String comment;
    @NotNull
    private Integer rate;
    @NotNull
    private List<String> imageUrl; // 🔄 수정
}

