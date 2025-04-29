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
}
