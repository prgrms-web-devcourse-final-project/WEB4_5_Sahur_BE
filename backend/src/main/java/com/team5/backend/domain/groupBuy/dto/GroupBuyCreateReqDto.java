package com.team5.backend.domain.groupBuy.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyCreateReqDto {
    @NotNull
    private Long productId;
    @NotNull
    private Integer targetParticipants;
    @NotNull
    private Integer round;
    @NotNull
    private LocalDateTime deadline;
}
