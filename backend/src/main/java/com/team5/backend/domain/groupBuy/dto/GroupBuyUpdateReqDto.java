package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class GroupBuyUpdateReqDto {
    private Integer minParticipants;
    private Integer currentParticipantCount;
    private Integer round;
    private LocalDateTime deadline;
    private GroupBuyStatus status;
}
