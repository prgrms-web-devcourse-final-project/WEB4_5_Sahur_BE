package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.product.entity.Product;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyResDto {
    private Long groupBuyId;
    private Product product;
    private Integer targetParticipants;
    private Integer currentParticipantCount;
    private Integer round;
    private LocalDateTime deadline;
    private GroupBuyStatus status;
    private boolean isDeadlineToday;

    public static GroupBuyResDto fromEntity(GroupBuy groupBuy, boolean isTodayDeadline) {

        return GroupBuyResDto.builder()
                .groupBuyId(groupBuy.getGroupBuyId())
                .product(groupBuy.getProduct())
                .targetParticipants(groupBuy.getTargetParticipants())
                .currentParticipantCount(groupBuy.getCurrentParticipantCount())
                .round(groupBuy.getRound())
                .deadline(groupBuy.getDeadline())
                .status(groupBuy.getStatus())
                .isDeadlineToday(isTodayDeadline)
                .build();
    }
}
