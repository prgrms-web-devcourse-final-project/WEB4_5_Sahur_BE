package com.team5.backend.domain.member.member.controller;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.member.member.service.MailService;
import com.team5.backend.domain.member.member.service.MemberService;
import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.RsDataUtil;
import com.team5.backend.global.security.PrincipalDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;
    private final MailService mailService;

    // 회원 생성
    @Operation(summary = "회원 가입", description = "새로운 회원을 등록합니다.")
    @PostMapping(value = "/auth/signup", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public RsData<SignupResDto> signup(
            @Parameter(description = "회원 데이터") @RequestPart("memberData") @Valid SignupReqDto signupReqDto,
            @Parameter(description = "프로필 이미지 (선택사항)") @RequestPart(value = "image", required = false) MultipartFile profileImage) throws IOException {

        SignupResDto signupResDto = memberService.signup(signupReqDto, profileImage);
        return RsDataUtil.success("회원가입에 성공했습니다.", signupResDto);
    }

    // 회원 조회
    @Operation(summary = "회원 정보 조회", description = "로그인한 회원의 정보를 조회합니다.")
    @GetMapping("/members/me")
    public RsData<GetMemberResDto> getMember(@AuthenticationPrincipal PrincipalDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();
        GetMemberResDto memberResDto = memberService.getMemberById(memberId);

        return RsDataUtil.success("회원 정보를 성공적으로 조회했습니다.", memberResDto);
    }

    // 회원 수정
    @Operation(summary = "회원 정보 수정", description = "회원 정보를 수정합니다.")
    @PatchMapping("/members/modify")
    public RsData<PatchMemberResDto> updateMember(@AuthenticationPrincipal PrincipalDetails userDetails,
                                                  @Parameter(description = "수정할 회원 정보") @Valid @RequestBody PatchMemberReqDto patchMemberReqDto) {

        Long memberId = userDetails.getMember().getMemberId();
        PatchMemberResDto updatedMember = memberService.updateMember(memberId, patchMemberReqDto);

        return RsDataUtil.success("회원 정보가 수정되었습니다.", updatedMember);
    }

    // 회원 탈퇴
    @Operation(summary = "회원 탈퇴", description = "회원 계정을 삭제하고 로그아웃 처리합니다.")
    @DeleteMapping("/members/delete")
    public RsData<Empty> deleteMember(@AuthenticationPrincipal PrincipalDetails userDetails, HttpServletResponse response) {

        Long memberId = userDetails.getMember().getMemberId();
        memberService.deleteMember(memberId, response);

        return RsDataUtil.success("로그아웃 및 회원 탈퇴가 완료되었습니다.");
    }

    // 회원 복구
    @Operation(summary = "회원 복구", description = "탈퇴한 회원을 복구합니다. 탈퇴한 회원이 로그인하면 발급되는 임시 토큰을 통해서만 호출할 수 있습니다.")
    @PostMapping("/restore")
    public RsData<MemberRestoreResDto> restoreMember(@AuthenticationPrincipal PrincipalDetails userDetails) {

        Long memberId = userDetails.getMember().getMemberId();
        MemberRestoreResDto memberRestoreResDto = memberService.restoreMember(memberId);

        return RsDataUtil.success("회원 복구가 완료되었습니다.", memberRestoreResDto);
    }

    // 로그인
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인합니다.")
    @PostMapping("/auth/login")
    public RsData<LoginResDto> login(
            @Parameter(description = "로그인 정보") @Valid @RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {

        LoginResDto loginResDto = authService.login(loginReqDto, response);
        return RsDataUtil.success("로그인에 성공했습니다.", loginResDto);
    }

    // 로그아웃
    @Operation(summary = "로그아웃", description = "현재 로그인된 계정을 로그아웃합니다.")
    @PostMapping("/auth/logout")
    public RsData<LogoutResDto> logout(
            @Parameter(description = "Access Token (Bearer 포함)") @RequestHeader(value = "Authorization", required = false) String token, HttpServletResponse response) {

        authService.logout(token, response);
        return RsDataUtil.success("로그아웃에 성공했습니다.", new LogoutResDto());
    }

    // 토큰 재발급
    @Operation(summary = "토큰 재발급", description = "리프레시 토큰을 사용하여 새로운 액세스 토큰을 발급합니다.")
    @PostMapping("/auth/refresh")
    public RsData<AuthResDto> refreshToken(
            @Parameter(description = "리프레시 토큰") @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        AuthResDto authResDto = authService.refreshToken(refreshToken, response);
        return RsDataUtil.success("토큰이 재발급되었습니다.", authResDto);
    }

    // 이메일 인증번호 전송
    @Operation(summary = "이메일 인증번호 전송", description = "회원가입 시 이메일 인증을 위한 인증번호를 전송합니다.")
    @PostMapping("/auth/email/send")
    public RsData<EmailResDto> requestAuthCode(
            @Parameter(description = "이메일 정보") @RequestBody @Valid EmailSendReqDto emailSendReqDto) throws MessagingException {

        EmailResDto response = mailService.sendAuthCode(emailSendReqDto);
        return RsDataUtil.success("인증번호가 이메일로 전송되었습니다.", response);
    }

    // 이메일 인증번호 검증
    @Operation(summary = "이메일 인증번호 검증", description = "전송된 이메일 인증번호의 유효성을 검증합니다.")
    @PostMapping("/auth/email/verify")
    public RsData<EmailResDto> validateAuthCode(
            @Parameter(description = "인증번호 검증 정보") @RequestBody @Valid EmailVerificationReqDto emailVerificationReqDto) {

        EmailResDto response = mailService.validationAuthCode(emailVerificationReqDto);
        return RsDataUtil.success("인증이 완료되었습니다.", response);
    }

    // 비밀번호 재설정 이메일 인증번호 전송
    @Operation(summary = "비밀번호 재설정 인증번호 전송", description = "비밀번호 재설정을 위한 인증번호를 이메일로 전송합니다.")
    @PostMapping("/auth/password/email/send")
    public RsData<EmailResDto> requestPasswordResetAuthCode(
            @Parameter(description = "이메일 정보") @RequestBody @Valid EmailSendReqDto emailSendReqDto) throws MessagingException {

        EmailResDto response = mailService.sendPasswordResetAuthCode(emailSendReqDto);
        return RsDataUtil.success("비밀번호 재설정 인증번호가 이메일로 전송되었습니다.", response);
    }

    // 비밀번호 재설정 이메일 인증번호 검증
    @Operation(summary = "비밀번호 재설정 인증번호 검증", description = "비밀번호 재설정을 위해 전송된 인증번호의 유효성을 검증합니다.")
    @PostMapping("/auth/password/email/verify")
    public RsData<EmailResDto> validatePasswordResetAuthCode(
            @Parameter(description = "인증번호 검증 정보") @RequestBody @Valid EmailVerificationReqDto emailVerificationReqDto) {

        EmailResDto response = mailService.verifyPasswordResetAuthCode(emailVerificationReqDto);
        return RsDataUtil.success("인증이 완료되었습니다. 새 비밀번호를 설정하세요.", response);
    }

    // 닉네임 중복 확인
    @Operation(summary = "닉네임 중복 확인", description = "사용하려는 닉네임의 중복 여부를 확인합니다.")
    @PostMapping("/members/nickname/check")
    public RsData<NicknameCheckResDto> checkNicknameDuplicate(
            @Parameter(description = "닉네임 정보") @RequestBody NicknameCheckReqDto nicknameCheckReqDto) {

        NicknameCheckResDto response = memberService.checkNicknameDuplicate(nicknameCheckReqDto.getNickname());
        return RsDataUtil.success("닉네임 중복 확인이 완료되었습니다.", response);
    }
}