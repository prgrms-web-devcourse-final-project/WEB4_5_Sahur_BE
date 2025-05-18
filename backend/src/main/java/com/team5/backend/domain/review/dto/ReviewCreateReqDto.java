package com.team5.backend.domain.review.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ReviewCreateReqDto {
    private Long historyId; // ✅ 구매 내역 선택
    private String comment;
    private Integer rate;
    private List<String> imageUrl;
}
