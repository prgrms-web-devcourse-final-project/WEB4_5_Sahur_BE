package com.team5.backend.domain.member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfoDto {
    private String email;
    private String role;
}
