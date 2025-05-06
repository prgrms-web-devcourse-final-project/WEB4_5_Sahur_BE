package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyPatchReqDto {

    private Integer targetParticipants;
    private Integer currentParticipantCount;
    private Integer round;
    private LocalDateTime deadline;
    private GroupBuyStatus status;
}