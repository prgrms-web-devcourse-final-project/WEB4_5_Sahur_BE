package com.team5.backend.domain.category.dto;

import com.team5.backend.domain.category.entity.CategoryType;
import com.team5.backend.domain.category.entity.KeywordType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryCreateReqDto {

    @NotNull(message = "카테고리는 필수입니다.")
    private CategoryType categoryType;

    @NotNull(message = "키워드는 필수입니다.")
    private KeywordType keyword;

}
