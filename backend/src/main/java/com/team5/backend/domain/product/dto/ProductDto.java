package com.team5.backend.domain.product.dto;

import com.team5.backend.domain.category.dto.CategoryDto;
import com.team5.backend.domain.product.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long productId;
    private String title;
    private String description;
    private List<String> imageUrl;
    private Integer price;
    private Long dibCount;
    private LocalDateTime createdAt;
    private CategoryDto category;

    public static ProductDto fromEntity(Product product) {
        return ProductDto.builder()
                .productId(product.getProductId())
                .title(product.getTitle())
                .description(product.getDescription())
                .imageUrl(product.getImageUrl())
                .price(product.getPrice())
                .dibCount(product.getDibCount())
                .createdAt(product.getCreatedAt())
                .category(CategoryDto.fromEntity(product.getCategory()))
                .build();
    }
}
