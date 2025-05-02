package com.team5.backend.domain.member.member.controller;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.member.member.service.MailService;
import com.team5.backend.domain.member.member.service.MemberService;
import com.team5.backend.global.dto.RsData;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final MailService mailService;

    // 회원 생성
    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResDto> signup(@Valid @RequestBody SignupReqDto signupReqDto) {

        SignupResDto signupResDto = memberService.signup(signupReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(signupResDto);
    }

    // 회원 조회
    @GetMapping("/members/me")
    public ResponseEntity<GetMemberResDto> getMember(@RequestHeader(value = "Authorization", required = false) String token) {

        GetMemberResDto loggedInMember = authService.getLoggedInMember(token);

        GetMemberResDto memberResDto = memberService.getMemberById(loggedInMember.getMemberId());
        return ResponseEntity.ok(memberResDto);
    }

    @PatchMapping("/members/modify")
    public ResponseEntity<GetMemberResDto> updateMember(@RequestHeader(value = "Authorization", required = false) String token,
                                                        @Valid @RequestBody PatchMemberReqDto patchMemberReqDto) {

        GetMemberResDto loggedInMember = authService.getLoggedInMember(token);

        GetMemberResDto updatedMember = memberService.updateMember(loggedInMember.getMemberId(), patchMemberReqDto);
        return ResponseEntity.ok(updatedMember);
    }

    // 회원 탈퇴
    @DeleteMapping("/members/delete")
    public ResponseEntity<Void> deleteMember(@RequestHeader(value = "Authorization", required = false) String token) {

        GetMemberResDto loggedInMember = authService.getLoggedInMember(token);

        memberService.deleteMember(loggedInMember.getMemberId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResDto> login(@Valid @RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {

        LoginResDto loginResDto = authService.login(loginReqDto, response);
        return ResponseEntity.ok(loginResDto);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<LogoutResDto> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.ok(new LogoutResDto());
    }

    /**
     * 리프레시 토큰을 사용하여 액세스 토큰 갱신 API
     * 필요한 경우 리프레시 토큰도 함께 갱신
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<AuthResDto> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        AuthResDto authResDto = authService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(authResDto);
    }

    // 이메일 인증번호 전송
    @PostMapping("/auth/email/send")
    public ResponseEntity<EmailResDto> requestAuthCode(@RequestBody @Valid EmailSendReqDto emailSendReqDto) throws MessagingException {

        EmailResDto response = mailService.sendAuthCode(emailSendReqDto);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    // 이메일 인증번호 검증
    @PostMapping("/auth/email/verify")
    public ResponseEntity<EmailResDto> validateAuthCode(@RequestBody @Valid EmailVerificationReqDto emailVerificationReqDto) {

        EmailResDto response = mailService.validationAuthCode(emailVerificationReqDto);

        return response.isSuccess()
                ? ResponseEntity.ok(response)
                : ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 비밀번호 재설정 이메일 인증번호 전송
    @PostMapping("/auth/password/email/send")
    public RsData<EmailResDto> requestPasswordResetAuthCode(@RequestBody @Valid EmailSendReqDto emailSendReqDto) throws MessagingException {

        EmailResDto response = mailService.sendPasswordResetAuthCode(emailSendReqDto);

        if (response.isSuccess()) return new RsData<>("200-1", "비밀번호 재설정 인증번호가 이메일로 전송되었습니다.", response);
        else return new RsData<>("400-1", "이메일 인증번호 전송에 실패했습니다.", response);
    }

    // 비밀번호 재설정 이메일 인증번호 검증
    @PostMapping("/auth/password/email/verify")
    public RsData<EmailResDto> validatePasswordResetAuthCode(@RequestBody @Valid EmailVerificationReqDto emailVerificationReqDto) {

        EmailResDto response = mailService.verifyPasswordResetAuthCode(emailVerificationReqDto);

        if (response.isSuccess()) return new RsData<>("200-1", "인증이 완료되었습니다. 새 비밀번호를 설정하세요.", response);
        else return new RsData<>("400-1", "인증번호가 유효하지 않거나 만료되었습니다.", response);
    }

    // 비밀번호 재설정
    @PatchMapping("/members/password/reset")
    public RsData<PasswordResetResDto> resetPassword(@Valid @RequestBody PasswordResetReqDto passwordResetReqDto) {
        PasswordResetResDto response = memberService.resetPassword(passwordResetReqDto);

        if (response.isSuccess()) return new RsData<>("200-1", "비밀번호가 성공적으로 재설정되었습니다.", response);
        else return new RsData<>("400-1", "비밀번호 재설정에 실패했습니다.", response);
    }
}
