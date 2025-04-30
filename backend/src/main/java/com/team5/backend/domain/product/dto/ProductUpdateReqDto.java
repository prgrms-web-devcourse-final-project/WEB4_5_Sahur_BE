package com.team5.backend.domain.product.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductUpdateReqDto {
    private String title;
    private String description;
    private String imageUrl;
    private Integer price;
}
