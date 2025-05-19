package com.team5.backend.domain.member.admin.dto;

import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestUpdateReqDto {

    @NotNull(message = "status 값은 필수입니다.")
    private ProductRequestStatus status;

}
