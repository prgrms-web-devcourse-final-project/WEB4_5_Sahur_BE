package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyResDto {
    private Long groupBuyId;
    private Long productId;
    private Long categoryId;
    private Integer targetParticipants;
    private Integer currentParticipantCount;
    private Integer round;
    private LocalDateTime deadline;
    private GroupBuyStatus status;

    // fromEntity 추가
    public static GroupBuyResDto fromEntity(GroupBuy groupBuy) {
        return GroupBuyResDto.builder()
                .groupBuyId(groupBuy.getGroupBuyId())
                .productId(groupBuy.getProduct().getProductId())
                .categoryId(groupBuy.getCategory().getCategoryId())
                .targetParticipants(groupBuy.getTargetParticipants())
                .currentParticipantCount(groupBuy.getCurrentParticipantCount())
                .round(groupBuy.getRound())
                .deadline(groupBuy.getDeadline())
                .status(groupBuy.getStatus())
                .build();
    }
}
