package com.team5.backend.domain.member.member.dto;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GetMemberResDto {

    private Long memberId;
    private String email;
    private String nickname;
    private String name;
    private String address;
    private String imageUrl;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GetMemberResDto fromEntity(Member member) {

        return GetMemberResDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .address(member.getAddress().toString())
                .imageUrl(member.getImageUrl())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .build();
    }
}
