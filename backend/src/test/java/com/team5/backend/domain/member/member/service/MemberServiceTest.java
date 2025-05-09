package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.domain.product.entity.Product;
import com.team5.backend.global.entity.Address;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.util.ImageUtil;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MemberServiceTest {

    @InjectMocks
    private MemberService memberService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MailService mailService;

    @Mock
    private AuthService authService;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private AuthTokenManager authTokenManager;

    @Mock
    private ImageUtil imageUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private HttpServletResponse response;

    private Member member;
    private SignupReqDto signupReqDto;
    private PatchMemberReqDto patchMemberReqDto;
    private MultipartFile mockProfileImage;
    private Authentication authentication;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {

        // 테스트용 회원
        member = Member.builder()
                .memberId(1L)
                .email("test@example.com")
                .nickname("테스트")
                .name("테스트")
                .password("encodedPassword")
                .address(new Address("12345", "서울시 강남구", "상세주소"))
                .imageUrl("/images/test-profile.jpg")
                .role(Role.USER)
                .emailVerified(true)
                .build();

        // 회원가입 DTO
        signupReqDto = SignupReqDto.builder()
                .email("test@example.com")
                .nickname("테스트")
                .name("테스트")
                .password("password123")
                .zipCode("12345")
                .streetAdr("서울시 강남구")
                .detailAdr("상세주소")
                .build();

        // 회원 정보 수정 DTO
        patchMemberReqDto = PatchMemberReqDto.builder()
                .email("updated@example.com")
                .nickname("수정테스트")
                .name("수정테스트")
                .password("newPassword123")
                .imageUrl("/images/updated-profile.jpg")
                .build();

        // 모의 프로필 이미지 파일 설정
        mockProfileImage = new MockMultipartFile(
                "profileImage",
                "test-image.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        // 인증 모의 설정
        authentication = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
    }

    @Test
    @DisplayName("회원가입 성공 테스트")
    void signupSuccess() throws IOException {

        // Given
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(memberRepository.existsByNickname(anyString())).willReturn(false);
        given(mailService.isEmailVerified(anyString())).willReturn(true);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(imageUtil.saveImage(any(MultipartFile.class))).willReturn("/images/test-profile.jpg");
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // When
        SignupResDto result = memberService.signup(signupReqDto, mockProfileImage);

        // Then
        assertNotNull(result);
        assertEquals(member.getMemberId(), result.getMemberId());
        assertEquals("회원가입이 성공적으로 완료되었습니다.", result.getMessage());

        // Verify
        verify(memberRepository).existsByEmail(signupReqDto.getEmail());
        verify(memberRepository).existsByNickname(signupReqDto.getNickname());
        verify(mailService).isEmailVerified(signupReqDto.getEmail());
        verify(passwordEncoder).encode(signupReqDto.getPassword());
        verify(imageUtil).saveImage(mockProfileImage);
        verify(memberRepository).save(any(Member.class));
        verify(mailService).clearEmailVerificationStatus(signupReqDto.getEmail());
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signupFailDueToEmailDuplicate() {

        // Given
        given(memberRepository.existsByEmail(anyString())).willReturn(true);

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.signup(signupReqDto, mockProfileImage);
        });

        assertEquals(MemberErrorCode.EMAIL_ALREADY_USED, exception.getErrorCode());

        // Verify
        verify(memberRepository).existsByEmail(signupReqDto.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 닉네임 중복")
    void signupFailNicknameDuplicate() {

        // Given
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(memberRepository.existsByNickname(anyString())).willReturn(true);

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.signup(signupReqDto, mockProfileImage);
        });

        assertEquals(MemberErrorCode.NICKNAME_ALREADY_USED, exception.getErrorCode());

        // Verify
        verify(memberRepository).existsByEmail(signupReqDto.getEmail());
        verify(memberRepository).existsByNickname(signupReqDto.getNickname());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 이메일 미인증")
    void signupFailDueToEmailNotVerified() {

        // Given
        given(memberRepository.existsByEmail(anyString())).willReturn(false);
        given(memberRepository.existsByNickname(anyString())).willReturn(false);
        given(mailService.isEmailVerified(anyString())).willReturn(false);

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.signup(signupReqDto, mockProfileImage);
        });

        assertEquals(MemberErrorCode.EMAIL_NOT_VERIFIED, exception.getErrorCode());

        // Verify
        verify(memberRepository).existsByEmail(signupReqDto.getEmail());
        verify(memberRepository).existsByNickname(signupReqDto.getNickname());
        verify(mailService).isEmailVerified(signupReqDto.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 조회 성공 테스트")
    void getMemberByIdSuccess() {

        // Given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        // When
        GetMemberResDto result = memberService.getMemberById(memberId);

        // Then
        assertNotNull(result);
        assertEquals(member.getMemberId(), result.getMemberId());
        assertEquals(member.getEmail(), result.getEmail());
        assertEquals(member.getNickname(), result.getNickname());
        assertEquals(member.getName(), result.getName());

        // Verify
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("회원 조회 실패 - 회원 없음")
    void getMemberByIdFailMemberNotFound() {

        // Given
        Long memberId = 99L;
        given(memberRepository.findById(memberId)).willReturn(Optional.empty());

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.getMemberById(memberId);
        });

        assertEquals(MemberErrorCode.MEMBER_NOT_FOUND, exception.getErrorCode());

        // Verify
        verify(memberRepository).findById(memberId);
    }

    @Test
    @DisplayName("회원 정보 수정 성공 테스트")
    void updateMemberSuccess() {

        // Given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByEmail(patchMemberReqDto.getEmail())).willReturn(false);
        given(memberRepository.existsByNickname(patchMemberReqDto.getNickname())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("newEncodedPassword");
        given(memberRepository.save(any(Member.class))).willReturn(member);

        // When
        PatchMemberResDto result = memberService.updateMember(memberId, patchMemberReqDto);

        // Then
        assertNotNull(result);
        assertEquals(memberId, result.getMemberId());
        assertEquals("회원 정보가 성공적으로 수정되었습니다.", result.getMessage());

        // Verify
        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmail(patchMemberReqDto.getEmail());
        verify(memberRepository).existsByNickname(patchMemberReqDto.getNickname());
        verify(passwordEncoder).encode(patchMemberReqDto.getPassword());
        verify(memberRepository).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 정보 수정 실패 - 이메일 중복")
    void updateMemberFailDueToEmailDuplicate() {

        // Given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));
        given(memberRepository.existsByEmail(patchMemberReqDto.getEmail())).willReturn(true);

        // When, Then
        CustomException exception = assertThrows(CustomException.class, () -> {
            memberService.updateMember(memberId, patchMemberReqDto);
        });

        assertEquals(MemberErrorCode.EMAIL_ALREADY_USED, exception.getErrorCode());

        // Verify
        verify(memberRepository).findById(memberId);
        verify(memberRepository).existsByEmail(patchMemberReqDto.getEmail());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("회원 삭제 성공 테스트")
    void deleteMemberSuccess() {

        // Given
        Long memberId = 1L;
        given(memberRepository.findById(memberId)).willReturn(Optional.of(member));

        given(securityContext.getAuthentication()).willReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        given(authTokenManager.extractToken(any(Authentication.class))).willReturn("valid-token");
        doNothing().when(authService).logout(anyString(), any(HttpServletResponse.class));

        // When
        memberService.deleteMember(memberId, response);

        // Then, Verify
        verify(memberRepository).findById(memberId);
        verify(authTokenManager).extractToken(authentication);
        verify(authService).logout(anyString(), any(HttpServletResponse.class));
        verify(memberRepository).delete(member);
    }

    @Test
    @DisplayName("리뷰 작성 가능한 상품 조회 테스트")
    void getReviewableProductsByMemberSuccess() {

        // Given
        Long memberId = 1L;
        Pageable pageable = PageRequest.of(0, 10);
        List<Product> productList = new ArrayList<>();
        Page<Product> productPage = new PageImpl<>(productList, pageable, 0);

        given(historyRepository.findWritableProductsByMemberId(eq(memberId), any(Pageable.class)))
                .willReturn(productPage);

        // When
        Page<ProductResDto> result = memberService.getReviewableProductsByMember(memberId, pageable);

        // Then
        assertNotNull(result);
        assertEquals(0, result.getTotalElements());

        // Verify
        verify(historyRepository).findWritableProductsByMemberId(eq(memberId), any(Pageable.class));
    }

    @Test
    @DisplayName("비밀번호 재설정 성공 테스트")
    void resetPasswordSuccess() {

        // Given
        PasswordResetReqDto passwordResetReqDto = new PasswordResetReqDto("test@example.com", "newPassword123");

        given(mailService.isPasswordResetVerified(passwordResetReqDto.getEmail())).willReturn(true);
        given(memberRepository.findByEmail(passwordResetReqDto.getEmail())).willReturn(Optional.of(member));
        given(passwordEncoder.encode(passwordResetReqDto.getPassword())).willReturn("newEncodedPassword");

        // When
        PasswordResetResDto result = memberService.resetPassword(passwordResetReqDto);

        // Then
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("비밀번호가 성공적으로 재설정되었습니다.", result.getMessage());

        // Verify
        verify(mailService).isPasswordResetVerified(passwordResetReqDto.getEmail());
        verify(memberRepository).findByEmail(passwordResetReqDto.getEmail());
        verify(passwordEncoder).encode(passwordResetReqDto.getPassword());
        verify(memberRepository).save(member);
        verify(mailService).clearPasswordResetVerificationStatus(passwordResetReqDto.getEmail());
    }

    @Test
    @DisplayName("비밀번호 재설정 실패 - 이메일 인증 안됨")
    void resetPasswordFailEmailNotVerified() {

        // Given
        PasswordResetReqDto passwordResetReqDto = new PasswordResetReqDto("test@example.com", "newPassword123");
        given(mailService.isPasswordResetVerified(passwordResetReqDto.getEmail())).willReturn(false);

        // When
        PasswordResetResDto result = memberService.resetPassword(passwordResetReqDto);

        // Then
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals("이메일 인증이 완료되지 않았거나 인증 시간이 만료되었습니다.", result.getMessage());

        // Verify
        verify(mailService).isPasswordResetVerified(passwordResetReqDto.getEmail());
        verify(memberRepository, never()).findByEmail(anyString());
        verify(memberRepository, never()).save(any(Member.class));
    }

    @Test
    @DisplayName("닉네임 중복 체크 테스트 - 중복 있음")
    void checkNicknameDuplicateExists() {

        // Given
        String nickname = "existingNickname";
        given(memberRepository.existsByNickname(nickname)).willReturn(true);

        // When
        NicknameCheckResDto result = memberService.checkNicknameDuplicate(nickname);

        // Then
        assertNotNull(result);
        assertTrue(result.isExists());

        // Verify
        verify(memberRepository).existsByNickname(nickname);
    }

    @Test
    @DisplayName("닉네임 중복 체크 테스트 - 중복 없음")
    void checkNicknameDuplicateNotExists() {

        // Given
        String nickname = "newNickname";
        given(memberRepository.existsByNickname(nickname)).willReturn(false);

        // When
        NicknameCheckResDto result = memberService.checkNicknameDuplicate(nickname);

        // Then
        assertNotNull(result);
        assertFalse(result.isExists());

        // Verify
        verify(memberRepository).existsByNickname(nickname);
    }
}