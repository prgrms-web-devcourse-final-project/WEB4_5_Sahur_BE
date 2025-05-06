package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupBuyStatusResDto {
    private Integer currentParticipantCount;
    private GroupBuyStatus status;

    // fromEntity 추가
    public static GroupBuyStatusResDto fromEntity(GroupBuy groupBuy) {
        return GroupBuyStatusResDto.builder()
                .currentParticipantCount(groupBuy.getCurrentParticipantCount())
                .status(groupBuy.getStatus())
                .build();
    }
}
