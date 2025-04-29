package com.team5.backend.domain.member.admin.dto;

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
}
