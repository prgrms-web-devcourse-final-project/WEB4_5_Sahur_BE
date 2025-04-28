package com.team5.backend.domain.product.dto;

import com.team5.backend.domain.product.entity.Product.ProductStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductUpdateReqDto {
    private String title;
    private String description;
    private String imageUrl;
    private Integer price;
    private ProductStatus status;
}
