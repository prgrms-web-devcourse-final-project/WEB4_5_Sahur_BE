package com.team5.backend.domain.dibs.dto;

import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.groupBuy.dto.GroupBuyDto;
import com.team5.backend.domain.product.dto.ProductDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DibsResDto {
    private Long dibsId;
    private Long memberId;
    private ProductDto product;
    private LocalDateTime createdAt;
    private GroupBuyDto groupBuy; // ✅ 추가

    // 공동구매가 없을 경우
    public static DibsResDto fromEntity(Dibs dibs) {
        return DibsResDto.builder()
                .dibsId(dibs.getDibsId())
                .memberId(dibs.getMember().getMemberId())
                .product(ProductDto.fromEntity(dibs.getProduct()))
                .createdAt(dibs.getCreatedAt())
                .groupBuy(null)
                .build();
    }

    // 공동구매가 있을 경우
    public static DibsResDto fromEntity(Dibs dibs, GroupBuyDto groupBuyDto) {
        return DibsResDto.builder()
                .dibsId(dibs.getDibsId())
                .memberId(dibs.getMember().getMemberId())
                .product(ProductDto.fromEntity(dibs.getProduct()))
                .createdAt(dibs.getCreatedAt())
                .groupBuy(groupBuyDto)
                .build();
    }
}
