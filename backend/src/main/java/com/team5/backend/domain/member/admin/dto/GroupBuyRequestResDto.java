package com.team5.backend.domain.member.admin.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupBuyRequestResDto {

    private Long groupBuyRequestId;
    private Long productId;
    private Long memberId;
}
