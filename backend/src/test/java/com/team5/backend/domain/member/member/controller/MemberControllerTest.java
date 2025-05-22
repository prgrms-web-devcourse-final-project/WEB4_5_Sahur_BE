//package com.team5.backend.domain.member.member.controller;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.team5.backend.domain.member.member.dto.*;
//import com.team5.backend.domain.member.member.entity.Member;
//import com.team5.backend.domain.member.member.entity.Role;
//import com.team5.backend.domain.member.member.service.AuthService;
//import com.team5.backend.domain.member.member.service.MailService;
//import com.team5.backend.domain.member.member.service.MemberService;
//import com.team5.backend.global.entity.Address;
//import com.team5.backend.global.security.PrincipalDetails;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.mock.web.MockCookie;
//import org.springframework.mock.web.MockMultipartFile;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.test.util.ReflectionTestUtils;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.context.WebApplicationContext;
//
//import java.nio.charset.StandardCharsets;
//import java.time.LocalDateTime;
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.doNothing;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@SpringBootTest
//@AutoConfigureMockMvc
//@Transactional
//class MemberControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @Autowired
//    private WebApplicationContext context;
//
//    @Autowired
//    private ObjectMapper objectMapper;
//
//    @Mock
//    private MemberService memberService;
//
//    @Mock
//    private AuthService authService;
//
//    @Mock
//    private MailService mailService;
//
//    private Member member;
//    private String accessToken;
//    private final String TEST_EMAIL = "test@example.com";
//    private final String TEST_PASSWORD = "password123!";
//    private final String TEST_NICKNAME = "testUser";
//
//    @BeforeEach
//    void setUp() {
//
//        MockitoAnnotations.openMocks(this);
//
//        // Spring 컨텍스트에서 MemberController 빈을 가져와, ReflectionTestUtils를 사용하여 해당 빈의 private 필드들에 값을 설정
//        MemberController memberController = context.getBean(MemberController.class);
//
//        ReflectionTestUtils.setField(memberController, "memberService", memberService);
//        ReflectionTestUtils.setField(memberController, "authService", authService);
//        ReflectionTestUtils.setField(memberController, "mailService", mailService);
//
//        // MockMvc 설정 - 테스트용 보안 필터를 적용하지 않음
//        mockMvc = MockMvcBuilders
//                .webAppContextSetup(context)
//                .build();
//
//        // 테스트용 회원 생성
//        Address address = new Address("12345", "서울시 강남구", "테스트 123");
//        member = Member.builder()
//                .memberId(1L)
//                .email(TEST_EMAIL)
//                .nickname(TEST_NICKNAME)
//                .name("테스트")
//                .password("encodedPassword")
//                .address(address)
//                .imageUrl("/images/default-profile.png")
//                .role(Role.USER)
//                .emailVerified(true)
//                .createdAt(LocalDateTime.now())
//                .updatedAt(LocalDateTime.now())
//                .build();
//
//        // SecurityContext에 인증 정보 설정
//        PrincipalDetails principalDetails = new PrincipalDetails(member, Collections.emptyMap());
//        Authentication authentication = new UsernamePasswordAuthenticationToken(
//                principalDetails, null, principalDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);
//    }
//
//
//    @Test
//    @DisplayName("회원 가입")
//    void signup() throws Exception {
//
//        // Given
//        SignupReqDto signupReqDto = SignupReqDto.builder()
//                .email(TEST_EMAIL)
//                .password(TEST_PASSWORD)
//                .nickname(TEST_NICKNAME)
//                .name("테스트")
//                .zipCode("12345")
//                .streetAdr("서울시 강남구")
//                .detailAdr("테스트로 123")
//                .build();
//
//        MockMultipartFile memberData = new MockMultipartFile(
//                "memberData",
//                "",
//                "application/json",
//                objectMapper.writeValueAsString(signupReqDto).getBytes(StandardCharsets.UTF_8)
//        );
//
//        MockMultipartFile profileImage = new MockMultipartFile(
//                "image",
//                "test-image.jpg",
//                "image/jpeg",
//                "test image content".getBytes()
//        );
//
//        SignupResDto signupResDto = SignupResDto.builder()
//                .memberId(1L)
//                .message("회원가입이 성공적으로 완료되었습니다.")
//                .build();
//
//        when(memberService.signup(any(SignupReqDto.class), any())).thenReturn(signupResDto);
//
//        // When, Then
//        mockMvc.perform(multipart("/api/v1/auth/signup")
//                        .file(memberData)
//                        .file(profileImage)
//                        .contentType(MediaType.MULTIPART_FORM_DATA))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.memberId").value(1L))
//                .andExpect(jsonPath("$.data.message").value("회원가입이 성공적으로 완료되었습니다."));
//    }
//
//    @Test
//    @DisplayName("로그인")
//    void login() throws Exception {
//
//        // Given
//        LoginReqDto loginReqDto = new LoginReqDto(TEST_EMAIL, TEST_PASSWORD, false);
//        LoginResDto loginResDto = new LoginResDto(accessToken, "refresh-token", 1L);
//
//        when(authService.login(any(LoginReqDto.class), any())).thenReturn(loginResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(loginReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.accessToken").value(accessToken))
//                .andExpect(jsonPath("$.data.refreshToken").value("refresh-token"))
//                .andExpect(jsonPath("$.data.memberId").value(1L));
//    }
//
//    @Test
//    @DisplayName("로그아웃")
//    void logout() throws Exception {
//
//        // Given
//        doNothing().when(authService).logout(anyString(), any());
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/logout")
//                        .header("Authorization", "Bearer " + accessToken))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"));
//    }
//
//    @Test
//    @DisplayName("회원 정보 조회")
//    void getMember() throws Exception {
//
//        // Given
//        GetMemberResDto memberResDto = GetMemberResDto.fromEntity(member);
//        when(memberService.getMemberById(anyLong())).thenReturn(memberResDto);
//
//        // When, Then
//        mockMvc.perform(get("/api/v1/members/me")
//                        .header("Authorization", "Bearer " + accessToken))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.memberId").value(member.getMemberId()))
//                .andExpect(jsonPath("$.data.email").value(member.getEmail()))
//                .andExpect(jsonPath("$.data.nickname").value(member.getNickname()));
//    }
//
//    @Test
//    @DisplayName("회원 정보 수정")
//    void updateMember() throws Exception {
//
//        // Given
//        PatchMemberReqDto patchMemberReqDto = PatchMemberReqDto.builder()
//                .email("test@example.com")
//                .nickname("newNickname")
//                .name("새이름")
//                .password("Password123!")
//                .zipCode("12345")
//                .streetAdr("서울시 강남구")
//                .detailAdr("101호")
//                .imageUrl("https://example.com/profile.jpg")
//                .build();
//
//        PatchMemberResDto patchMemberResDto = new PatchMemberResDto(member.getMemberId(), "회원 정보가 성공적으로 수정되었습니다.");
//        when(memberService.updateMember(anyLong(), any(PatchMemberReqDto.class))).thenReturn(patchMemberResDto);
//
//        // When, Then
//        mockMvc.perform(patch("/api/v1/members/modify")
//                        .header("Authorization", "Bearer " + accessToken)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(patchMemberReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.memberId").value(member.getMemberId()));
//    }
//
//    @Test
//    @DisplayName("회원 탈퇴")
//    void deleteMember() throws Exception {
//
//        // Given
//        doNothing().when(memberService).deleteMember(anyLong(), any());
//
//        // When, Then
//        mockMvc.perform(delete("/api/v1/members/delete")
//                        .header("Authorization", "Bearer " + accessToken))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"));
//    }
//
//    @Test
//    @DisplayName("회원 복구")
//    void restoreMember() throws Exception {
//
//        // Given
//        MemberRestoreResDto restoreResDto = new MemberRestoreResDto(1L, "회원이 성공적으로 복구되었습니다.");
//        when(memberService.restoreMember(anyLong())).thenReturn(restoreResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/members/restore")
//                        .header("Authorization", "Bearer " + accessToken))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.memberId").value(1L));
//    }
//
//    @Test
//    @DisplayName("토큰 재발급")
//    void refreshToken() throws Exception {
//
//        // Given
//        String refreshToken = "refresh-token";
//        AuthResDto authResDto = new AuthResDto("new-access-token", "new-refresh-token");
//
//        when(authService.refreshToken(anyString(), any())).thenReturn(authResDto);
//
//        MockCookie cookie = new MockCookie("refreshToken", refreshToken);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/refresh")
//                        .cookie(cookie))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.accessToken").value("new-access-token"))
//                .andExpect(jsonPath("$.data.refreshToken").value("new-refresh-token"));
//    }
//
//    @Test
//    @DisplayName("이메일 인증번호 전송")
//    void sendEmailAuthCode() throws Exception {
//
//        // Given
//        EmailSendReqDto emailSendReqDto = new EmailSendReqDto(TEST_EMAIL);
//        EmailResDto emailResDto = EmailResDto.builder()
//                .success(true)
//                .message("인증번호가 발송되었습니다.")
//                .build();
//
//        when(mailService.sendAuthCode(any(EmailSendReqDto.class))).thenReturn(emailResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/email/send")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(emailSendReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.success").value(true));
//    }
//
//    @Test
//    @DisplayName("이메일 인증번호 검증")
//    void verifyEmailAuthCode() throws Exception {
//
//        // Given
//        EmailVerificationReqDto verificationReqDto = new EmailVerificationReqDto(TEST_EMAIL, "123456");
//        EmailResDto emailResDto = EmailResDto.builder()
//                .success(true)
//                .message("인증이 완료되었습니다.")
//                .build();
//
//        when(mailService.validationAuthCode(any(EmailVerificationReqDto.class))).thenReturn(emailResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/email/verify")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(verificationReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.success").value(true));
//    }
//
//    @Test
//    @DisplayName("비밀번호 재설정 인증번호 전송")
//    void sendPasswordResetAuthCodeSuccess() throws Exception {
//
//        // Given
//        EmailSendReqDto emailSendReqDto = new EmailSendReqDto(TEST_EMAIL);
//        EmailResDto emailResDto = EmailResDto.builder()
//                .success(true)
//                .message("비밀번호 재설정 인증번호가 발송되었습니다.")
//                .build();
//
//        when(mailService.sendPasswordResetAuthCode(any(EmailSendReqDto.class))).thenReturn(emailResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/password/email/send")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(emailSendReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.success").value(true));
//    }
//
//    @Test
//    @DisplayName("비밀번호 재설정 인증번호 검증")
//    void verifyPasswordResetAuthCodeSuccess() throws Exception {
//
//        // Given
//        EmailVerificationReqDto verificationReqDto = new EmailVerificationReqDto(TEST_EMAIL, "123456");
//        EmailResDto emailResDto = EmailResDto.builder()
//                .success(true)
//                .message("인증이 완료되었습니다.")
//                .build();
//
//        when(mailService.verifyPasswordResetAuthCode(any(EmailVerificationReqDto.class))).thenReturn(emailResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/auth/password/email/verify")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(verificationReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.success").value(true));
//    }
//
//    @Test
//    @DisplayName("비밀번호 재설정")
//    void resetPasswordSuccess() throws Exception {
//
//        // Given
//        PasswordResetReqDto resetReqDto = new PasswordResetReqDto(TEST_EMAIL, "newpassword123!");
//        PasswordResetResDto resetResDto = PasswordResetResDto.builder()
//                .success(true)
//                .message("비밀번호가 성공적으로 재설정되었습니다.")
//                .build();
//
//        when(memberService.resetPassword(any(PasswordResetReqDto.class))).thenReturn(resetResDto);
//
//        // When, Then
//        mockMvc.perform(patch("/api/v1/members/password/reset")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(resetReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.success").value(true));
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 확인 - 사용 가능")
//    void checkNicknameDuplicateAvailable() throws Exception {
//
//        // Given
//        NicknameCheckReqDto checkReqDto = new NicknameCheckReqDto("newUser");
//        NicknameCheckResDto checkResDto = NicknameCheckResDto.builder()
//                .exists(false)
//                .build();
//
//        when(memberService.checkNicknameDuplicate(anyString())).thenReturn(checkResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/members/nickname/check")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"))
//                .andExpect(jsonPath("$.data.exists").value(false));
//    }
//
//    @Test
//    @DisplayName("닉네임 중복 확인 - 이미 사용 중")
//    void checkNicknameDuplicateInUse() throws Exception {
//
//        // Given
//        NicknameCheckReqDto checkReqDto = new NicknameCheckReqDto(TEST_NICKNAME);
//        NicknameCheckResDto checkResDto = NicknameCheckResDto.builder()
//                .exists(true)
//                .build();
//
//        when(memberService.checkNicknameDuplicate(anyString())).thenReturn(checkResDto);
//
//        // When, Then
//        mockMvc.perform(post("/api/v1/members/nickname/check")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(checkReqDto)))
//                .andDo(print())
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.success").value(true))
//                .andExpect(jsonPath("$.msg").value("SUCCESS"));
//    }
//}