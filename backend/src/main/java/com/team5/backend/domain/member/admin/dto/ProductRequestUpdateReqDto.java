package com.team5.backend.domain.member.admin.dto;

import com.team5.backend.domain.member.admin.entity.ProductRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ProductRequestUpdateReqDto {

    @NotNull(message = "status 값은 필수입니다.")
    private ProductRequestStatus status;

}
