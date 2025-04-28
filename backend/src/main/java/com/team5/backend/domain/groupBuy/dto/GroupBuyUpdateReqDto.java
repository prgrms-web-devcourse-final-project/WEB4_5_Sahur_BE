package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class GroupBuyUpdateReqDto {
    private Integer minParticipants;
    private Integer currentParticipants;
    private Integer round;
    private LocalDateTime deadline;
    private GroupBuyStatus status;
}
