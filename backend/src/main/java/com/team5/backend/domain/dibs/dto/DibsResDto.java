package com.team5.backend.domain.dibs.dto;

import com.team5.backend.domain.dibs.entity.Dibs;
import com.team5.backend.domain.product.dto.ProductResDto;
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
    private ProductResDto product;
    private LocalDateTime createdAt;

    public static DibsResDto fromEntity(Dibs dibs) {
        return DibsResDto.builder()
                .dibsId(dibs.getDibsId())
                .memberId(dibs.getMember().getMemberId())
                .product(ProductResDto.fromEntity(dibs.getProduct()))
                .createdAt(dibs.getCreatedAt())
                .build();
    }
}
