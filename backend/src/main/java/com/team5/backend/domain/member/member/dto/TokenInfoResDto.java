package com.team5.backend.domain.member.member.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TokenInfoResDto {

    private String email;
    private String role;
}
