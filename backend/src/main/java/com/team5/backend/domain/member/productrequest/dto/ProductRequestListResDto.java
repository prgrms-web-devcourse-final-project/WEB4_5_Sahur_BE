package com.team5.backend.domain.member.productrequest.dto;

import com.team5.backend.domain.category.dto.CategoryDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ProductRequestListResDto {

    private Long productRequestId;
    private String title;
    private CategoryDto category;
    private LocalDateTime createdAt;
    private ProductRequestStatus status;

    public static ProductRequestListResDto fromEntity(ProductRequest productRequest) {
        return ProductRequestListResDto.builder()
                .productRequestId(productRequest.getProductRequestId())
                .title(productRequest.getTitle())
                .category(CategoryDto.fromEntity(productRequest.getCategory()))
                .createdAt(productRequest.getCreatedAt())
                .status(productRequest.getStatus())
                .build();
    }
}
