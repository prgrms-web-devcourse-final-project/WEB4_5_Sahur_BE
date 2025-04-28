package com.team5.backend.domain.dibs.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DibsCreateReqDto {
    private Long memberId;
    private Long productId;
}
