package com.team5.backend.domain.history.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class HistoryCreateReqDto {

    @NotNull(message = "회원 ID는 필수입니다.")
    private Long memberId;

    @NotNull(message = "상품 ID는 필수입니다.")
    private Long productId;

    @NotNull(message = "공동구매 ID는 필수입니다.")
    private Long groupBuyId;

    @NotNull(message = "주문 ID는 필수입니다.")
    private Long orderId;

    @NotNull(message = "작성 가능 여부는 필수입니다.")
    private Boolean writable;

}

