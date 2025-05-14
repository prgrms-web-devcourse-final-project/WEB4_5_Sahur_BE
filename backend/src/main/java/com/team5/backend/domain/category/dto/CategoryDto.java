package com.team5.backend.domain.category.dto;

import com.team5.backend.domain.category.entity.Category;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CategoryDto {
    private Long categoryId;
    private CategoryType category;
    private KeywordType keyword;
    private Integer uid;

    public static CategoryDto fromEntity(Category category) {
        return CategoryDto.builder()
                .categoryId(category.getCategoryId())
                .category(category.getCategory())
                .keyword(category.getKeyword())
                .uid(category.getUid())
                .build();
    }
}
