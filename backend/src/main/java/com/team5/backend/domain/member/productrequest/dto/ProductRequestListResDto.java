package com.team5.backend.domain.member.productrequest.dto;

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
    private Long categoryId;
    private LocalDateTime createdAt;
    private ProductRequestStatus status;

    public static ProductRequestListResDto fromEntity(ProductRequest pr) {
        return ProductRequestListResDto.builder()
                .productRequestId(pr.getProductRequestId())
                .title(pr.getTitle())
                .categoryId(pr.getCategory().getCategoryId())
                .createdAt(pr.getCreatedAt())
                .status(pr.getStatus())
                .build();
    }
}
