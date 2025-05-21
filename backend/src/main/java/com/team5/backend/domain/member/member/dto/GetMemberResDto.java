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
    private String phoneNumber;
    private String zipCode;
    private String streetAdr;
    private String detailAdr;
    private String imageUrl;
    private Role role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Boolean isLoggedIn; // 로그인 여부

    public static GetMemberResDto fromEntity(Member member) {

        return GetMemberResDto.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .nickname(member.getNickname())
                .name(member.getName())
                .phoneNumber(member.getPhoneNumber())
                .zipCode(member.getAddress().getZipCode())
                .streetAdr(member.getAddress().getStreetAdr())
                .detailAdr(member.getAddress().getDetailAdr())
                .imageUrl(member.getImageUrl())
                .role(member.getRole())
                .createdAt(member.getCreatedAt())
                .updatedAt(member.getUpdatedAt())
                .isLoggedIn(true) // 엔티티에서 생성 시 로그인 상태는 true
                .build();
    }
}
