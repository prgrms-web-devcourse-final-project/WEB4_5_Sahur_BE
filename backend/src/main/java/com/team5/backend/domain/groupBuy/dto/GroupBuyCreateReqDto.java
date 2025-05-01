package com.team5.backend.domain.groupBuy.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyCreateReqDto {
    private Long productId;
    private Long categoryId;
    private Integer targetParticipants;
    private Integer round;
    private LocalDateTime deadline;
}
