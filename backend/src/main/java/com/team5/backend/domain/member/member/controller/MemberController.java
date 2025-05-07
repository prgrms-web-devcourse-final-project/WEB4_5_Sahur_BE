package com.team5.backend.domain.member.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.member.member.service.MailService;
import com.team5.backend.domain.member.member.service.MemberService;
import com.team5.backend.global.dto.RsData;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final MailService mailService;
    private final ObjectMapper objectMapper;

    // 회원 생성
    @PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<SignupResDto> signup(@RequestPart("memberData") @Valid SignupReqDto signupReqDto,
                                       @RequestPart(value = "image", required = false) MultipartFile profileImage) throws IOException {

        SignupResDto signupResDto = memberService.signup(signupReqDto, profileImage);
        return new RsData<>("201", "회원가입에 성공했습니다.", signupResDto);
    }

    // 회원 조회
    @GetMapping("/members/me")
    public RsData<GetMemberResDto> getMember(@RequestHeader(value = "Authorization", required = false) String token) {

        GetMemberResDto loggedInMember = authService.getLoggedInMember(token);
        GetMemberResDto memberResDto = memberService.getMemberById(loggedInMember.getMemberId());

        return new RsData<>("200", "회원 정보를 성공적으로 조회했습니다.", memberResDto);
    }

    // 회원 수정
    @PatchMapping("/members/modify")
    public RsData<PatchMemberResDto> updateMember(@RequestHeader(value = "Authorization", required = false) String token, @Valid @RequestBody PatchMemberReqDto patchMemberReqDto) {

        GetMemberResDto loggedInMember = authService.getLoggedInMember(token);
        PatchMemberResDto updatedMember = memberService.updateMember(loggedInMember.getMemberId(), patchMemberReqDto);

        return new RsData<>("200", "회원 정보가 수정되었습니다.", updatedMember);
    }

    // 회원 탈퇴
    @DeleteMapping("/members/delete")
    public RsData<Void> deleteMember(@RequestHeader(value = "Authorization", required = false) String token, HttpServletResponse response) {

        memberService.deleteMember(token, response);
        return new RsData<>("200", "로그아웃 및 회원 탈퇴가 완료되었습니다.", null);
    }

    // 로그인
    @PostMapping("/auth/login")
    public RsData<LoginResDto> login(@Valid @RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {

        LoginResDto loginResDto = authService.login(loginReqDto, response);
        return new RsData<>("200", "로그인에 성공했습니다.", loginResDto);
    }

    // 로그아웃
    @PostMapping("/auth/logout")
    public RsData<LogoutResDto> logout(@RequestHeader(value = "Authorization", required = false) String token,
                                       HttpServletResponse response) {

        authService.logout(token, response);
        return new RsData<>("200", "로그아웃에 성공했습니다.", new LogoutResDto());
    }

    // 토큰 재발급
    @PostMapping("/auth/refresh")
    public RsData<AuthResDto> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {
        AuthResDto authResDto = authService.refreshToken(refreshToken, response);
        return new RsData<>("200", "토큰이 재발급되었습니다.", authResDto);
    }

    // 이메일 인증번호 전송
    @PostMapping("/auth/email/send")
    public RsData<EmailResDto> requestAuthCode(@RequestBody @Valid EmailSendReqDto emailSendReqDto) throws MessagingException {

        EmailResDto response = mailService.sendAuthCode(emailSendReqDto);

        if (response.isSuccess()) return new RsData<>("200", "인증번호가 이메일로 전송되었습니다.", response);
        else return new RsData<>("400", "이메일 인증번호 전송에 실패했습니다.", response);
    }

    // 이메일 인증번호 검증
    @PostMapping("/auth/email/verify")
    public RsData<EmailResDto> validateAuthCode(@RequestBody @Valid EmailVerificationReqDto emailVerificationReqDto) {

        EmailResDto response = mailService.validationAuthCode(emailVerificationReqDto);

        if (response.isSuccess()) return new RsData<>("200", "인증이 완료되었습니다.", response);
        else return new RsData<>("400", "인증번호가 유효하지 않거나 만료되었습니다.", response);
    }

    // 비밀번호 재설정 이메일 인증번호 전송
    @PostMapping("/auth/password/email/send")
    public RsData<EmailResDto> requestPasswordResetAuthCode(@RequestBody @Valid EmailSendReqDto emailSendReqDto) throws MessagingException {

        EmailResDto response = mailService.sendPasswordResetAuthCode(emailSendReqDto);

        if (response.isSuccess()) return new RsData<>("200", "비밀번호 재설정 인증번호가 이메일로 전송되었습니다.", response);
        else return new RsData<>("400", "이메일 인증번호 전송에 실패했습니다.", response);
    }

    // 비밀번호 재설정 이메일 인증번호 검증
    @PostMapping("/auth/password/email/verify")
    public RsData<EmailResDto> validatePasswordResetAuthCode(@RequestBody @Valid EmailVerificationReqDto emailVerificationReqDto) {

        EmailResDto response = mailService.verifyPasswordResetAuthCode(emailVerificationReqDto);

        if (response.isSuccess()) return new RsData<>("200", "인증이 완료되었습니다. 새 비밀번호를 설정하세요.", response);
        else return new RsData<>("400", "인증번호가 유효하지 않거나 만료되었습니다.", response);
    }

    // 비밀번호 재설정
    @PatchMapping("/members/password/reset")
    public RsData<PasswordResetResDto> resetPassword(@Valid @RequestBody PasswordResetReqDto passwordResetReqDto) {

        PasswordResetResDto response = memberService.resetPassword(passwordResetReqDto);

        if (response.isSuccess()) return new RsData<>("200", "비밀번호가 성공적으로 재설정되었습니다.", response);
        else return new RsData<>("400", "비밀번호 재설정에 실패했습니다.", response);
    }

    @PostMapping("/members/nickname/check")
    public RsData<NicknameCheckResDto> checkNicknameDuplicate(@RequestBody NicknameCheckReqDto nicknameCheckReqDto) {

        NicknameCheckResDto response = memberService.checkNicknameDuplicate(nicknameCheckReqDto.getNickname());

        if (response.isExists()) return new RsData<>("400", "이미 사용 중인 닉네임입니다.", response);
        else return new RsData<>("200", "사용 가능한 닉네임입니다.", response);
    }
}
