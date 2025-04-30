package com.team5.backend.domain.product.dto;

import com.team5.backend.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ProductResDto {
    private Long productId;
    private Long categoryId;
    private String title;
    private String description;
    private String imageUrl;
    private Integer price;
    private Long dibCount;
    private LocalDateTime createdAt;

    public static ProductResDto fromEntity(Product product) {
        return ProductResDto.builder()
                .productId(product.getProductId())
                .categoryId(product.getCategoryId())
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .dibCount(product.getDibCount())
                .createdAt(product.getCreatedAt())
                .build();
    }

}
