package com.team5.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreateReqDto {
    private Long categoryId;
    private String title;
    private String description;
    private String imageUrl;
    private Integer price;
}
