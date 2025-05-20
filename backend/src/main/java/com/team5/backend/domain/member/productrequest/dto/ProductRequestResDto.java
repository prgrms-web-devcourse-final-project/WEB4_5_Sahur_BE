package com.team5.backend.domain.member.productrequest.dto;

import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class ProductRequestResDto {

    private Long productRequestId;
    private Long memberId;
    private Long categoryId;
    private String title;
    private String productUrl;
    private String etc;
    private List<String> imageUrls;
    private String description;
    private ProductRequestStatus status;

    public static ProductRequestResDto fromEntity(ProductRequest productRequest) {
        return ProductRequestResDto.builder()
                .productRequestId(productRequest.getProductRequestId())
                .memberId(productRequest.getMember().getMemberId())
                .categoryId(productRequest.getCategory().getCategoryId())
                .title(productRequest.getTitle())
                .productUrl(productRequest.getProductUrl())
//                .etc(productRequest.getEtc())
                .imageUrls(productRequest.getImageUrls())
                .description(productRequest.getDescription())
                .status(productRequest.getStatus())
                .build();
    }
}
