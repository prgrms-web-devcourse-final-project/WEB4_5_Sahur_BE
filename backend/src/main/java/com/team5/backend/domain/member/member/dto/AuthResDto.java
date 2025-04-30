package com.team5.backend.domain.member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthResDto {

    private String accessToken;
    private String refreshToken;

    public AuthResDto(String accessToken) {

        this.accessToken = accessToken;
        this.refreshToken = accessToken;
    }
}