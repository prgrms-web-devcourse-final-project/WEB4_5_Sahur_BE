package com.team5.backend.domain.member.member.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class NicknameCheckResDto {
    private boolean exists;
}
