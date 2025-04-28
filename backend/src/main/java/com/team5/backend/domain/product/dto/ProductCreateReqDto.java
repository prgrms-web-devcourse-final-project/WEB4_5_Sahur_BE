package com.team5.backend.domain.product.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductCreateReqDto {
    private Long memberId;
    private String title;
    private String description;
    private String imageUrl;
    private Integer price;
}
