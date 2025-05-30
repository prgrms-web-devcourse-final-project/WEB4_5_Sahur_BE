package com.team5.backend.domain.member.productrequest.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;
import java.util.List;
import jakarta.validation.constraints.NotEmpty;


import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestCreateReqDto {
    @NotNull(message = "카테고리 ID는 필수입니다.")
    private Long categoryId;

    @NotBlank(message = "상품명은 필수입니다.")
    private String title;

    @NotBlank(message = "상품 URL은 필수입니다.")
    @URL(message = "유효한 URL 형식이 아닙니다.")
    private String productUrl;

    @NotBlank(message = "상품 설명은 필수입니다.")
    private String description;

}
