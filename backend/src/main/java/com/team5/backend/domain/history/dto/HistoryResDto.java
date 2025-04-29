package com.team5.backend.domain.history.dto;

import com.team5.backend.domain.history.entity.History;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryResDto {

    private Long historyId;
    private Long memberId;
    private Long productId;
    private Long groupBuyId;
    private Boolean writable;

    public static HistoryResDto fromEntity(History history) {
        return HistoryResDto.builder()
                .historyId(history.getHistoryId())
                .memberId(history.getMember().getMemberId())
                .productId(history.getProduct().getProductId())
                .groupBuyId(history.getGroupBuy().getGroupBuyId())
                .writable(history.getWritable())
                .build();
    }
}
