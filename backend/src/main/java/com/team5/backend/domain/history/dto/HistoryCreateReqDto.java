package com.team5.backend.domain.history.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class HistoryCreateReqDto {

    private Long memberId;
    private Long productId;
    private Boolean writable;
}

