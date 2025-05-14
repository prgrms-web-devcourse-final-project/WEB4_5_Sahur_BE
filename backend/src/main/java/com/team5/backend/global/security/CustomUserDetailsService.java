package com.team5.backend.global.security;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.MemberErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 인증 시점에서 검증 상태 확인 (선택적)
        if (!member.getEmailVerified()) {
            throw new UsernameNotFoundException("검증되지 않은 사용자입니다.");
        }

        // 삭제된 회원은 일반 인증에서 제외
        if (member.getDeleted()) {
            throw new UsernameNotFoundException("탈퇴한 회원입니다: " + email);
        }

        return new PrincipalDetails(member, null);  // OAuth2 인증에 필요한 attributes는 null로 설정
    }

    public UserDetails loadUserByOAuth2User(OAuth2User oAuth2User) throws OAuth2AuthenticationException {

        Member member = memberRepository.findByEmail(oAuth2User.getName())
                .orElseThrow(() -> new OAuth2AuthenticationException("Member not found"));

        // 검증 상태 확인 (선택적)
        if (!member.getEmailVerified()) {
            throw new OAuth2AuthenticationException("검증되지 않은 사용자입니다.");
        }

        // OAuth2User의 attributes를 PrincipalDetails로 전달
        return new PrincipalDetails(member, oAuth2User.getAttributes());
    }

    // 삭제된 회원 복구를 위한 메서드
    public UserDetails loadDeletedUserByUsername(String email) throws UsernameNotFoundException {

        Member deletedMember = memberRepository.findByEmailAllMembers(email)
                .orElseThrow(() -> {
                    return new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
                });

        // 삭제되지 않은 회원이라면 이 메서드를 사용하면 안됨
        if (deletedMember.getDeletedAt() == null) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_DELETED);
        }

        return new PrincipalDetails(deletedMember, Collections.emptyMap());
    }
}
