package com.team5.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewPatchReqDto {
    private String comment;
    private Integer rate;
    private List<String> imageUrl; // 🔄 수정
}

