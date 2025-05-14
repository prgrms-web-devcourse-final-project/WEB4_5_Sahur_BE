package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.entity.Address;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private AuthTokenManager authTokenManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;


    private Member member;
    private String email;
    private String password;
    private String encPassword;
    private String accessToken;
    private String refreshToken;

    @BeforeEach
    void setUp() {
        
        email = "test@example.com";
        password = "password123";
        encPassword = "endPassword123";
        accessToken = "test.access.token";
        refreshToken = "test.refresh.token";

        member = Member.builder()
                .memberId(1L)
                .email(email)
                .password(encPassword)
                .nickname("테스트")
                .role(Role.USER)
                .build();
    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void loginSuccess() {
        
        // Given
        LoginReqDto loginReqDto = new LoginReqDto(email, password, false);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encPassword)).thenReturn(true);
        when(jwtUtil.generateAccessToken(member.getMemberId(), email, member.getRole().name()))
                .thenReturn(accessToken);
        when(jwtUtil.generateRefreshToken(member.getMemberId(), email, member.getRole().name()))
                .thenReturn(refreshToken);
        when(jwtUtil.getAccessTokenExpiration()).thenReturn(3600000L);
        when(jwtUtil.getRefreshTokenExpiration()).thenReturn(86400000L);

        // When
        LoginResDto result = authService.login(loginReqDto, response);

        // Then
        assertNotNull(result);
        assertEquals(accessToken, result.getAccessToken());
        assertEquals(refreshToken, result.getRefreshToken());
        assertEquals(member.getMemberId(), result.getMemberId());

        verify(authTokenManager).addCookie(response, "accessToken", accessToken, 3600);
        verify(authTokenManager).addCookie(response, "refreshToken", refreshToken, 86400);
    }

    @Test
    @DisplayName("로그인 실패 - 이메일 없음")
    void loginFailEmailNotFound() {
        
        // Given
        LoginReqDto loginReqDto = new LoginReqDto(email, password, false);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () ->
                authService.login(loginReqDto, response));

        assertEquals(AuthErrorCode.INVALID_LOGIN_INFO, exception.getErrorCode());
        verify(jwtUtil, never()).generateAccessToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void loginFailPasswordMismatch() {

        // Given
        LoginReqDto loginReqDto = new LoginReqDto(email, password, false);

        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(member));
        when(passwordEncoder.matches(password, encPassword)).thenReturn(false);

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> authService.login(loginReqDto, response));

        assertEquals(AuthErrorCode.INVALID_LOGIN_INFO, exception.getErrorCode());
        verify(jwtUtil, never()).generateAccessToken(anyLong(), anyString(), anyString());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logoutTest() {

        // Given
        String token = "Bearer " + accessToken;

        // When
        authService.logout(token, response);

        // Then
        verify(authTokenManager).invalidateTokens(accessToken, response);
    }

    @Test
    @DisplayName("토큰 갱신 테스트")
    void refreshTokenTest() {

        // Given
        AuthResDto expectedAuthResDto = new AuthResDto(accessToken, refreshToken);
        when(authTokenManager.refreshTokens(isNull(), eq(refreshToken), eq(response)))
                .thenReturn(expectedAuthResDto);

        // When
        AuthResDto result = authService.refreshToken(refreshToken, response);

        // Then
        assertNotNull(result);
        assertEquals(expectedAuthResDto.getAccessToken(), result.getAccessToken());
        assertEquals(expectedAuthResDto.getRefreshToken(), result.getRefreshToken());
        verify(authTokenManager).refreshTokens(null, refreshToken, response);
    }

    @Test
    @DisplayName("로그인된 사용자 정보 조회 성공")
    void getLoggedInMemberSuccess() {

        // Given
        String token = "Bearer " + accessToken;

        // fromEntity 메소드에서 Address를 처리할 수 있도록 주소 설정
        Address address = mock(Address.class);
        when(address.toString()).thenReturn("Test Address");

        // Address를 포함한 Member 객체 생성
        Member memberWithAddress = Member.builder()
                .memberId(member.getMemberId())
                .email(member.getEmail())
                .password(member.getPassword())
                .nickname(member.getNickname())
                .role(member.getRole())
                .address(address)  // null이 아닌 Address 객체 설정
                .build();

        when(authTokenManager.validateAccessToken(accessToken)).thenReturn(email);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.of(memberWithAddress));

        // When
        GetMemberResDto result = authService.getLoggedInMember(token);

        // Then
        assertNotNull(result);
        assertEquals(memberWithAddress.getMemberId(), result.getMemberId());
        assertEquals(memberWithAddress.getEmail(), result.getEmail());
        assertEquals(memberWithAddress.getNickname(), result.getNickname());
        assertEquals(memberWithAddress.getRole(), result.getRole());
        assertNotNull(result.getAddress()); // 주소가 null이 아닌지 확인
    }

    @Test
    @DisplayName("로그인된 사용자 정보 조회 실패 - 회원 정보 없음")
    void getLoggedInMemberFailMemberNotFound() {

        // Given
        String token = "Bearer " + accessToken;

        when(authTokenManager.validateAccessToken(accessToken)).thenReturn(email);
        when(memberRepository.findByEmail(email)).thenReturn(Optional.empty());

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> authService.getLoggedInMember(token));

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("토큰 정보 추출 성공")
    void extractTokenInfoSuccess() {

        // Given
        when(jwtUtil.isTokenExpired(accessToken)).thenReturn(false);
        when(jwtUtil.extractEmail(accessToken)).thenReturn(email);
        when(jwtUtil.extractRole(accessToken)).thenReturn(Role.USER.name());

        // When
        TokenInfoResDto result = authService.extractTokenInfo(accessToken);

        // Then
        assertNotNull(result);
        assertEquals(email, result.getEmail());
        assertEquals(Role.USER.name(), result.getRole());
    }

    @Test
    @DisplayName("토큰 정보 추출 실패 - 만료된 토큰")
    void extractTokenInfoFailExpiredToken() {

        // Given
        when(jwtUtil.isTokenExpired(accessToken)).thenReturn(true);

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> authService.extractTokenInfo(accessToken));

        assertEquals(AuthErrorCode.EXPIRED_TOKEN, exception.getErrorCode());
        verify(jwtUtil, never()).extractEmail(anyString());
    }
}