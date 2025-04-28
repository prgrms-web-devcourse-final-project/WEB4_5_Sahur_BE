package com.team5.backend.domain.product.dto;

import com.team5.backend.domain.product.entity.Product.ProductStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResDto {
    private Long productId;
    private String title;
    private String description;
    private String imageUrl;
    private Integer price;
    private Long dibCount;
    private LocalDateTime createdAt;
    private ProductStatus status;
}
