package com.team5.backend.domain.delivery.dto;

import com.team5.backend.global.entity.Address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryReqDto {

    private String zipCode;

    @NotBlank(message = "도로명/지번 주소는 필수 입력 항목입니다.")
    private String streetAdr;

    @NotBlank(message = "상세 주소는 필수 입력 항목입니다.")
    private String detailAdr;

    private Integer pccc;

    @NotBlank(message = "연락처는 필수입니다.")
    private String contact;

    // Address 객체로 변환하는 메소드
    public Address toAddress() {
        return new Address(zipCode, streetAdr, detailAdr);
    }
}
