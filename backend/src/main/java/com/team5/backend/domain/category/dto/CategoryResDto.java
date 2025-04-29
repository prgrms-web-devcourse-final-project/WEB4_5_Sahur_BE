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
    private Long productId;
    private CategoryType category;
    private KeywordType keyword;
    private Integer uid;

    public static CategoryResDto fromEntity(Category category) {
        return CategoryResDto.builder()
                .categoryId(category.getCategoryId())
                .productId(category.getProduct().getProductId())
                .category(category.getCategory())
                .keyword(category.getKeyword())
                .uid(category.getUid())
                .build();
    }
}
