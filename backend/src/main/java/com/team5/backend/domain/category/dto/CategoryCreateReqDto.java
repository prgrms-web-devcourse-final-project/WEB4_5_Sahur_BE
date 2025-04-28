package com.team5.backend.domain.category.dto;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateReqDto {
    private Long productId;
    private CategoryType category;
    private KeywordType keyword;
    private Integer uid;
}
