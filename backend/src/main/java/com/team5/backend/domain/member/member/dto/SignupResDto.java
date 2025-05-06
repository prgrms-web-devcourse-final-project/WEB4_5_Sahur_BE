package com.team5.backend.domain.member.member.dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SignupResDto {

    private Long memberId;
    private String message;
}
