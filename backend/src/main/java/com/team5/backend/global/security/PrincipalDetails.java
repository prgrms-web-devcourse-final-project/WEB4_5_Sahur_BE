package com.team5.backend.global.security;

import com.team5.backend.domain.member.member.entity.Member;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Collection;
import java.util.Map;

public class PrincipalDetails implements UserDetails, OAuth2User {

    private Member member;
    private Map<String, Object> attributes; // OAuth2User에서 가져온 추가 정보

    public PrincipalDetails(Member member, Map<String, Object> attributes) {

        this.member = member;
        this.attributes = attributes;
    }

    @Override
    public String getUsername() {
        return member.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getPassword() {
        return member.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.createAuthorityList(member.getRole().name());
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    // OAuth2User 인터페이스 구현
    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    public boolean emailVerified() {
        return member.getEmailVerified();  // 이메일 인증 여부를 체크
    }

    public Member getMember() {
        return this.member;
    }
}
