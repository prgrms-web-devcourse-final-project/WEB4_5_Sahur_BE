package com.team5.backend.global.security;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));

        // 인증 시점에서 검증 상태 확인 (선택적)
        if (!member.getEmailVerified()) {
            throw new UsernameNotFoundException("검증되지 않은 사용자입니다.");
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
}
