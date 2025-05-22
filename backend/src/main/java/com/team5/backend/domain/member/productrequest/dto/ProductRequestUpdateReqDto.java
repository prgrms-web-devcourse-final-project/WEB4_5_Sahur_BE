package com.team5.backend.domain.member.productrequest.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.URL;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestUpdateReqDto {

    private Long categoryId;

    private String title;

    @URL(message = "유효한 URL 형식이 아닙니다.")
    private String productUrl;

    private List<String> imageUrls;

    private String description;
}
