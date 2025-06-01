package com.team5.backend.domain.history.dto;

import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.order.dto.OrderHistoryInfoDto;
import com.team5.backend.domain.product.dto.ProductDto;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class HistoryResDto {

    private Long historyId;
    private Long memberId;
    private ProductDto productDto;
    private Long groupBuyId;
    private OrderHistoryInfoDto order;  // ✅ 변경된 부분
    private Boolean writable;

    public static HistoryResDto fromEntity(History history) {
        return HistoryResDto.builder()
                .historyId(history.getHistoryId())
                .memberId(history.getMember().getMemberId())
                .productDto(ProductDto.fromEntity(history.getProduct()))
                .groupBuyId(history.getGroupBuy().getGroupBuyId())
                .order(OrderHistoryInfoDto.fromEntity(history.getOrder())) // ✅ 변경된 부분
                .writable(history.getWritable())
                .build();
    }
}
