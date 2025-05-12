package com.team5.backend.domain.delivery.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReqDto {

    @NotBlank(message = "주소는 필수입니다.")
    private String address;

    private Integer pccc;

    @NotBlank(message = "연락처는 필수입니다.")
    private String contact;

    @NotBlank(message = "운송장 정보는 필수입니다.")
    private String shipping;
}
