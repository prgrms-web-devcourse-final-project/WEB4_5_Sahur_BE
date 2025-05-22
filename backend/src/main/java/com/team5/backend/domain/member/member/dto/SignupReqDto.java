package com.team5.backend.domain.member.member.dto;

import com.team5.backend.global.entity.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SignupReqDto {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "이름은 필수 입력 항목입니다.")
    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하로 입력해주세요.")
    private String name;

    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하로 입력해주세요.")
    private String nickname;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 최소 8자, 영문/숫자/특수문자를 포함해야 합니다.")
    private String password;

    @NotBlank(message = "휴대폰 번호는 필수입니다.")
    @Pattern(regexp = "^01\\d{8,9}$", message = "유효한 휴대폰 번호 형식이 아닙니다. (예: 01012345678)")
    private String phoneNumber;

    @NotBlank(message = "우편번호는 필수 입력 항목입니다.")
    private String zipCode;

    @NotBlank(message = "도로명/지번 주소는 필수 입력 항목입니다.")
    private String streetAdr;

    @NotBlank(message = "상세 주소는 필수 입력 항목입니다.")
    private String detailAdr;

    // Address 객체로 변환하는 메소드
    public Address toAddress() {
        return new Address(zipCode, streetAdr, detailAdr);
    }
}
