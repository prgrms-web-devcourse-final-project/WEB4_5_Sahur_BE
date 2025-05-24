package com.team5.backend.domain.member.member.dto;

import com.team5.backend.global.entity.Address;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.validator.constraints.URL;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PatchMemberReqDto {

    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하여야 합니다.")
    private String nickname;

    @Size(min = 2, max = 50, message = "이름은 2자 이상 50자 이하여야 합니다.")
    private String name;

    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$",
            message = "비밀번호는 8자 이상이며, 영문자, 숫자, 특수문자를 포함해야 합니다.")
    private String password;

    @Pattern(regexp = "^01\\d{8,9}$", message = "유효한 휴대폰 번호 형식이 아닙니다. (예: 01012345678)")
    private String phoneNumber;

    private String zipCode;

    private String streetAdr;

    private String detailAdr;

    @URL(message = "유효한 URL 형식이 아닙니다.")
    private String imageUrl;

    // Address 객체로 변환하는 메소드
    public Address toAddress() {
        return new Address(zipCode, streetAdr, detailAdr);
    }
}