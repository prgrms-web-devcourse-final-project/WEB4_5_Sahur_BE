package com.team5.backend.domain.member.member.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class TokenRefreshResDto {

    private String accessToken;
    private String refreshToken;
    private boolean tokenRefreshed;
}
