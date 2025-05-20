package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.security.MemberTokenInfo;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
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

        MemberTokenInfo memberTokenInfo = MemberTokenInfo.from(member);

        // 액세스 토큰 및 리프레시 토큰 생성
        String accessToken = jwtUtil.generateAccessToken(memberTokenInfo);
        String refreshToken = jwtUtil.generateRefreshToken(memberTokenInfo);

        // Remember Me 쿠키 설정 (사용자가 Remember Me를 선택했을 경우)
        if (loginReqDto.isRememberMe()) {

            String rememberMeToken = jwtUtil.generateRememberMeToken(memberTokenInfo);

            int rememberMeMaxAge = (int) (jwtUtil.getRememberMeExpiration() / 1000);
            authTokenManager.addCookie(response, "rememberMe", rememberMeToken, rememberMeMaxAge);
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

        MemberTokenInfo memberTokenInfo = MemberTokenInfo.from(member);

        // 짧은 유효기간의 임시 액세스 토큰 생성 (리프레시 토큰 없음)
        String tempAccessToken = jwtUtil.generateDeletedMemberToken(memberTokenInfo);

        // 쿠키에 임시 토큰 설정 (5분)
        int tempTokenMaxAge = (int) (jwtUtil.getTemporaryTokenExpiration() / 1000);
        authTokenManager.addCookie(response, "accessToken", tempAccessToken, tempTokenMaxAge);

        // 리프레시 토큰은 null로 반환 (복구 목적으로만 사용)
        return new LoginResDto(tempAccessToken, null, member.getMemberId());
    }

    // 로그아웃 메서드
    @Transactional
    public void logout(String headerToken, HttpServletRequest request, HttpServletResponse response) {

        // 토큰 추출
        String accessToken = extractToken(headerToken, request);

        // 토큰 무효화 로직 수행
        invalidateMember(accessToken, response);
    }

    private void invalidateMember(String accessToken, HttpServletResponse response) {

        checkAccessToken(accessToken);

        // 토큰에서 이메일 추출
        String email;
        boolean isDeletedMemberToken;

        try {
            email = jwtUtil.extractEmail(accessToken);
            isDeletedMemberToken = jwtUtil.isDeletedMemberToken(accessToken);
        } catch (Exception e) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // 삭제된 회원의 토큰인 경우
        if (isDeletedMemberToken) {

            invalidateDeletedMemberToken(accessToken, response);
            return ;
        }

        // 일반 회원에 대한 토큰 유효성 검증
        if (!jwtUtil.validateToken(accessToken, email)) {
            throw new CustomException(AuthErrorCode.INVALID_TOKEN);
        }

        // 일반 회원의 세션 무효화
        invalidateActiveMember(email, accessToken, response);
    }

    private static void checkAccessToken(String accessToken) {

        if (accessToken == null || accessToken.isEmpty()) {
            throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
        }
    }

    private void invalidateActiveMember(String email, String accessToken, HttpServletResponse response) {

        // 리프레시 토큰 처리
        invalidateRefreshToken(email);

        // Remember Me 토큰 처리
        invalidateRememberMeToken(email);

        // 액세스 토큰 블랙리스트에 추가
        jwtUtil.addToBlacklist(accessToken);

        // 쿠키 삭제
        authTokenManager.deleteCookies(response);
    }

    private void invalidateRememberMeToken(String email) {

        try {
            jwtUtil.invalidateRememberMeToken(email);
        } catch (Exception e) {
            log.warn("Remember Me 토큰 처리 중 예외 발생: {}", e.getMessage());
        }

    }

    private void invalidateRefreshToken(String email) {

        String refreshToken = jwtUtil.getStoredRefreshToken(email);

        if (refreshToken != null) {
            jwtUtil.addRefreshTokenToBlacklist(refreshToken);
            jwtUtil.removeRefreshToken(email);
        } else {
            // 일반 회원이지만 리프레시 토큰이 없는 경우 - 비정상 케이스
            log.warn("일반 회원의 리프레시 토큰이 존재하지 않습니다. 이메일: {}", email);
        }
    }

    private void invalidateDeletedMemberToken(String accessToken, HttpServletResponse response) {

        // 액세스 토큰 블랙리스트에 추가
        jwtUtil.addToBlacklist(accessToken);

        // 쿠키 삭제
        authTokenManager.deleteCookies(response);
    }

    private String extractToken(String headerToken, HttpServletRequest request) {

        String accessToken = headerToken;

        // 헤더에 액세스 토큰이 없으면 쿠키에서 확인
        if (accessToken == null || accessToken.isEmpty()) {
            accessToken = authTokenManager.extractAccessToken(request);
        } else if (accessToken.startsWith("Bearer ")) {
            // Bearer 접두사가 있는 경우 제거
            accessToken = accessToken.substring(7);
        }

        // 액세스 토큰이 여전히 비어있으면 예외 처리
        checkAccessToken(accessToken);

        return accessToken;
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

    @Transactional
    public MemberRestoreResDto handleMemberRestoreAuth(Member restoredMember, HttpServletResponse response) {

        MemberTokenInfo memberTokenInfo = MemberTokenInfo.from(restoredMember);

        // 회원 복구 후 인증 처리 (토큰 발급 및 쿠키 저장)
        String accessToken = jwtUtil.generateAccessToken(memberTokenInfo);
        String refreshToken = jwtUtil.generateRefreshToken(memberTokenInfo);

        // 토큰을 쿠키에 저장
        int accessTokenMaxAge = (int) (jwtUtil.getAccessTokenExpiration() / 1000);
        int refreshTokenMaxAge = (int) (jwtUtil.getRefreshTokenExpiration() / 1000);

        authTokenManager.addCookie(response, "accessToken", accessToken, accessTokenMaxAge);
        authTokenManager.addCookie(response, "refreshToken", refreshToken, refreshTokenMaxAge);

        return MemberRestoreResDto.builder()
                .memberId(restoredMember.getMemberId())
                .message("회원 복구가 성공적으로 완료되었습니다.")
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }
}