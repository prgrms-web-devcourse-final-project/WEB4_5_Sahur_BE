package com.team5.backend.domain.member.admin.dto;

import com.team5.backend.domain.member.admin.entity.GroupBuyRequest;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupBuyRequestResDto {

    private Long groupBuyRequestId;
    private Long productId;
    private Long memberId;

    public static GroupBuyRequestResDto fromEntity(GroupBuyRequest groupBuyRequest) {
        return GroupBuyRequestResDto.builder()
                .groupBuyRequestId(groupBuyRequest.getGroupBuyRequestId())
                .productId(groupBuyRequest.getProduct().getProductId())
                .memberId(groupBuyRequest.getMember().getMemberId())
                .build();
    }
}
