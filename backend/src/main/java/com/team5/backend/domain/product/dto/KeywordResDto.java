package com.team5.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KeywordResDto {
    private String keyword;
    private Double score;
}