package com.team5.backend.domain.member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;

    public AuthResponseDto(String accessToken) {

        this.accessToken = accessToken;
        this.refreshToken = accessToken;
    }
}