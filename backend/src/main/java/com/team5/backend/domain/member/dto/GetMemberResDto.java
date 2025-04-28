package com.team5.backend.domain.member.dto;

import com.team5.backend.domain.member.entity.Member;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
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
    private String role;
    private LocalDateTime createdAt;

    public static GetMemberResDto fromEntity(Member member) {

        return GetMemberResDto.builder()
                .memberId(member.getId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .address(member.getAddress())
                .imageUrl(member.getImageUrl())
                .role(member.getRole().name())
                .createdAt(member.getCreatedAt())
                .build();
    }
}
