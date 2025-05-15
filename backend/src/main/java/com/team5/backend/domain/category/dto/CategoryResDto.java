package com.team5.backend.domain.category.dto;

import com.team5.backend.domain.category.entity.Category;
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
public class CategoryResDto {
    private Long categoryId;
    private CategoryType categoryType;
    private KeywordType keyword;
    private Integer uid;

    public static CategoryResDto fromEntity(Category category) {
        return CategoryResDto.builder()
                .categoryId(category.getCategoryId())
                .categoryType(category.getCategoryType())
                .keyword(category.getKeyword())
                .uid(category.getUid())
                .build();
    }
}
