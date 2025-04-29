package com.team5.backend.domain.member.admin.dto;

import com.team5.backend.domain.member.admin.entity.ProductRequest;
import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductRequestResDto {

    private Long productRequestId;
    private Long memberId;
    private Long categoryId;
    private String title;
    private String productUrl;
    private String etc;
    private ProductRequestStatus status;

    public static ProductRequestResDto fromEntity(ProductRequest productRequest) {
        return ProductRequestResDto.builder()
                .productRequestId(productRequest.getProductRequestId())
                .memberId(productRequest.getMember().getMemberId())
                .categoryId(productRequest.getCategory().getCategoryId())
                .title(productRequest.getTitle())
                .productUrl(productRequest.getProductUrl())
                .etc(productRequest.getEtc())
                .status(productRequest.getStatus())
                .build();
    }
}
