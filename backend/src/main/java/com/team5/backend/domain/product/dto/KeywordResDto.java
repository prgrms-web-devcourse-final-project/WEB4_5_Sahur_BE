package com.team5.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 인기 키워드 응답 DTO
 * Redis Sorted Set에서 조회된 keyword 및 score 정보를 담는다.
 */
@Getter
@AllArgsConstructor
public class KeywordResDto {
    private String keyword;
    private Double score;
}