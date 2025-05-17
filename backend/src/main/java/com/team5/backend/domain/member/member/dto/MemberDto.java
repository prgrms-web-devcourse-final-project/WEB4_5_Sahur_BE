package com.team5.backend.domain.member.member.dto;

import com.team5.backend.domain.member.member.entity.Member;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MemberDto {
    private Long memberId;
    private String nickname;
    private String imageUrl;

    public static MemberDto fromEntity(Member member) {
        return MemberDto.builder()
                .memberId(member.getMemberId())
                .nickname(member.getNickname())
                .imageUrl(member.getImageUrl())
                .build();
    }
}
