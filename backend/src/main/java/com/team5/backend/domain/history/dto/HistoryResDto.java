package com.team5.backend.domain.history.dto;

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

    public static HistoryResDto fromEntity(Long historyId, Long memberId, Long productId, Long groupBuyId, Boolean writable) {
        return HistoryResDto.builder()
                .historyId(historyId)
                .memberId(memberId)
                .productId(productId)
                .groupBuyId(groupBuyId)
                .writable(writable)
                .build();
    }
}
