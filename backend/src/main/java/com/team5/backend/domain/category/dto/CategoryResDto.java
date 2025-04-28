package com.team5.backend.domain.category.dto;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResDto {
    private Integer categoryId;
    private Long productId;
    private CategoryType category;
    private KeywordType keyword;
    private Integer uid;
}
