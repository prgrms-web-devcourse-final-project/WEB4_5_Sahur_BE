package com.team5.backend.domain.product.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateReqDto {

    @NotNull(message = "카테고리는 필수입니다.")
    private Long categoryId;

    @NotNull(message = "제목은 필수입니다.")
    private String title;

    @NotNull(message = "상품 설명은 필수입니다.")
    private String description;

    @NotNull(message = "가격은 필수입니다.")
    private Integer price;
}
