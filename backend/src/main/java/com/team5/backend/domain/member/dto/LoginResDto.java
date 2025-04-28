package com.team5.backend.domain.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginResDto {

    private String accessToken;
    private String refreshToken;
    private Long memberId;
}
