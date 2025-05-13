package com.team5.backend.domain.groupBuy.dto;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import com.team5.backend.domain.product.dto.ProductDto;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GroupBuyDetailResDto {
    private Long groupBuyId;
    private ProductDto product;
    private Double averageRate;
    private Integer targetParticipants;
    private Integer currentParticipantCount;
    private Integer round;
    private LocalDateTime deadline;
    private GroupBuyStatus status;
    private boolean isDeadlineToday;
    private boolean isDibs;

    public static GroupBuyDetailResDto fromEntity(GroupBuy groupBuy, boolean isTodayDeadline, boolean isDibs, Double averageRate) {
        return GroupBuyDetailResDto.builder()
                .groupBuyId(groupBuy.getGroupBuyId())
                .product(ProductDto.fromEntity(groupBuy.getProduct()))
                .averageRate(averageRate)
                .targetParticipants(groupBuy.getTargetParticipants())
                .currentParticipantCount(groupBuy.getCurrentParticipantCount())
                .round(groupBuy.getRound())
                .deadline(groupBuy.getDeadline())
                .status(groupBuy.getStatus())
                .isDeadlineToday(isTodayDeadline)
                .isDibs(isDibs)
                .build();
    }
}
