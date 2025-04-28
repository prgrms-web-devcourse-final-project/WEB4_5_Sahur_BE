package com.team5.backend.domain.category.dto;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryUpdateReqDto {
    private CategoryType category;
    private KeywordType keyword;
    private Integer uid;
}
