package com.team5.backend.global.security;

import com.team5.backend.domain.member.member.entity.Member;

public record MemberTokenInfo(Long memberId, String email, String role) {

    public static MemberTokenInfo from(Member member) {
        return new MemberTokenInfo(
                member.getMemberId(),
                member.getEmail(),
                member.getRole().name()
        );
    }
}
