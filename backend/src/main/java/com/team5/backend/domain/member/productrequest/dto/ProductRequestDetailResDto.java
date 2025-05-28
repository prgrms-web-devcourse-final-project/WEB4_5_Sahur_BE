package com.team5.backend.domain.member.productrequest.dto;

import com.team5.backend.domain.category.dto.CategoryDto;
import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductRequestDetailResDto {

    private Long productRequestId;
    private Long memberId;
    private CategoryDto category;
    private String title;
    private String productUrl;
    private List<String> imageUrls;
    private String description;
    private ProductRequestStatus status;

    public static ProductRequestDetailResDto fromEntity(ProductRequest productRequest) {
        return ProductRequestDetailResDto.builder()
                .productRequestId(productRequest.getProductRequestId())
                .memberId(productRequest.getMember().getMemberId())
                .category(CategoryDto.fromEntity(productRequest.getCategory()))
                .title(productRequest.getTitle())
                .productUrl(productRequest.getProductUrl())
                .imageUrls(productRequest.getImageUrls())
                .description(productRequest.getDescription())
                .status(productRequest.getStatus())
                .build();
    }
}
