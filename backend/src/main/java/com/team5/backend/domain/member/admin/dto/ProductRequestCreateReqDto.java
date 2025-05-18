package com.team5.backend.domain.member.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

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

    private String etc;

    @NotEmpty(message = "이미지는 최소 1개 이상 등록해야 합니다.")
    private List<String> imageUrls;

    @NotBlank(message = "상품 설명은 필수입니다.")
    private String description;

}
