package com.team5.backend.domain.groupBuy.dto;

import lombok.Getter;
import java.time.LocalDateTime;

@Getter
public class GroupBuyCreateReqDto {
    private Long productId;
    private Long categoryId;
    private Integer minParticipants;
    private Integer round;
    private LocalDateTime deadline;
}
