package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.util.JwtUtil;
import com.team5.backend.global.security.AuthTokenManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthTokenManager authTokenManager;

    @Transactional
    public LoginResDto login(LoginReqDto loginReqDto, HttpServletResponse response) {

        // 이메일로 회원 조회 (삭제된 회원 포함)
        Member member = memberRepository.findByEmailAllMembers(loginReqDto.getEmail())
                .orElseThrow(() -> new CustomException(AuthErrorCode.INVALID_LOGIN_INFO));

        // 비밀번호 확인
        if (!passwordEncoder.matches(loginReqDto.getPassword(), member.getPassword())) {
            throw new CustomException(AuthErrorCode.INVALID_LOGIN_INFO);
        }

        // 탈퇴한 회원인 경우 - 임시 토큰 발급
        if (member.getDeleted()) {
            return deleteMemberLogin(member, response);
        }

        // 액세스 토큰 및 리프레시 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(member.getMemberId(), member.getEmail(), member.getRole().name());
        String refreshToken = jwtUtil.generateRefreshToken(member.getMemberId(), member.getEmail(), member.getRole().name());

        // Remember Me 쿠키 설정 (사용자가 Remember Me를 선택했을 경우)
        if (loginReqDto.isRememberMe()) {

            String rememberMeToken = jwtUtil.generateRememberMeToken(member.getMemberId(), member.getEmail(), member.getRole().name());

            int rememberMeMaxAge = (int) (jwtUtil.getRememberMeExpiration() / 1000);
            authTokenManager.addCookie(response, "remember-me", rememberMeToken, rememberMeMaxAge);
        }

        // 기존 쿠키 설정 로직
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);

        authTokenManager.addCookie(response, "accessToken", accessToken, accessTokenMaxAge);
        authTokenManager.addCookie(response, "refreshToken", refreshToken, refreshTokenMaxAge);

        // 로그인 응답 DTO 생성 및 반환
        return new LoginResDto(accessToken, refreshToken, member.getMemberId());
    }

    private LoginResDto deleteMemberLogin(Member member, HttpServletResponse response) {

        // 짧은 유효기간의 임시 액세스 토큰 생성 (리프레시 토큰 없음)
        String tempAccessToken = jwtUtil.generateDeletedMemberToken(
                member.getMemberId(),
                member.getEmail(),
                member.getRole().name()
        );

        // 쿠키에 임시 토큰 설정 (5분)
        int tempTokenMaxAge = (int) (jwtUtil.getTemporaryTokenExpiration() / 1000);
        authTokenManager.addCookie(response, "accessToken", tempAccessToken, tempTokenMaxAge);

        // 리프레시 토큰은 null로 반환 (복구 목적으로만 사용)
        return new LoginResDto(tempAccessToken, null, member.getMemberId());
    }

    // 로그아웃 메서드
    @Transactional
    public void logout(String token, HttpServletResponse response) {

        String accessToken = token;

        // Bearer 접두사가 있는 경우 제거
        if (accessToken.startsWith("Bearer ")) {
            accessToken = accessToken.substring(7);
        }

        // TokenCookieUtil을 사용하여 모든 토큰 무효화 및 쿠키 삭제
        authTokenManager.invalidateTokensWithRememberMe(accessToken, response);
    }

    // 토큰 갱신 메서드
    @Transactional
    public AuthResDto refreshToken(String refreshToken, HttpServletResponse response) {
        return authTokenManager.refreshTokens(null, refreshToken, response);
    }

    // 로그인된 사용자의 정보를 반환하는 메서드
    public GetMemberResDto getLoggedInMember(String token) {

        // Bearer 접두사가 있는 경우 제거
        String extractedToken = token.replace("Bearer ", "");

        // TokenCookieUtil을 사용하여 토큰 검증
        String email = authTokenManager.validateAccessToken(extractedToken);

        return memberRepository.findByEmail(email)
                .map(GetMemberResDto::fromEntity)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));
    }

    // 토큰에서 사용자 정보를 추출하는 메서드
    public TokenInfoResDto extractTokenInfo(String token) {

        if (jwtUtil.isTokenExpired(token)) {
            throw new CustomException(AuthErrorCode.EXPIRED_TOKEN);
        }

        String email = jwtUtil.extractEmail(token);
        String role = jwtUtil.extractRole(token);

        return new TokenInfoResDto(email, role);
    }
}