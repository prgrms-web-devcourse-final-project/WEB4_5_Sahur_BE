package com.team5.backend.domain.member.member.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PatchMemberResDto {

    private Long memberId;
    private String message;
}
