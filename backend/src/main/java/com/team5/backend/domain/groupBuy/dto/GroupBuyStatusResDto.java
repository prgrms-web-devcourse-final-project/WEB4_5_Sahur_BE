package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupBuyStatusResDto {
    private Integer currentParticipants;
    private GroupBuyStatus status;
}
