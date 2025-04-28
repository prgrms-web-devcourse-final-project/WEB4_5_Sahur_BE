package com.team5.backend.domain.dibs.dto;

import com.team5.backend.domain.dibs.entity.Dibs;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DibsResDto {
    private Long dibsId;
    private Long memberId;
    private Long productId;
    private Boolean status;

    public static DibsResDto fromEntity(Dibs dibs) {
        return DibsResDto.builder()
                .dibsId(dibs.getDibsId())
                .memberId(dibs.getMember().getMemberId())
                .productId(dibs.getProduct().getProductId())
                .status(dibs.getStatus())
                .build();
    }
}
