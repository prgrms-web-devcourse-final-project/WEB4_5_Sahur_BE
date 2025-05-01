package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyUpdateReqDto {

    @NotNull
    private Integer targetParticipants;

    @NotNull
    private Integer currentParticipantCount;

    @NotNull
    private Integer round;

    @NotNull
    private LocalDateTime deadline;

    @NotNull
    private GroupBuyStatus status;
}
