package com.team5.backend.domain.member.member.controller;

import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.service.AuthService;
import com.team5.backend.domain.member.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;
    private final AuthService authService;

    // 회원 생성
    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResDto> signup(@Valid @RequestBody SignupReqDto signupReqDto) {

        SignupResDto signupResDto = memberService.signup(signupReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(signupResDto);
    }

    // 회원 조회
    @GetMapping("/members/{memberId}")
    public ResponseEntity<GetMemberResDto> getMember(@PathVariable Long memberId) {

        GetMemberResDto memberResDto = memberService.getMemberById(memberId);
        return ResponseEntity.ok(memberResDto);
    }

    // 회원 전체 조회
    @GetMapping("/members")
    public ResponseEntity<List<GetMemberResDto>> getAllMembers() {

        List<GetMemberResDto> members = memberService.getAllMembers();
        return ResponseEntity.ok(members);
    }

    @PutMapping("/members/{memberId}")
    public ResponseEntity<GetMemberResDto> updateMember(@PathVariable Long memberId, @Valid @RequestBody UpdateMemberReqDto updateMemberReqDto) {

        GetMemberResDto updatedMember = memberService.updateMember(memberId, updateMemberReqDto);
        return ResponseEntity.ok(updatedMember);
    }

    // 회원 탈퇴
    @DeleteMapping("/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {

        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/auth/login")
    public ResponseEntity<LoginResDto> login(@Valid @RequestBody LoginReqDto loginReqDto, HttpServletResponse response) {

        LoginResDto loginResDto = authService.login(loginReqDto, response);
        return ResponseEntity.ok(loginResDto);
    }

    @PostMapping("/auth/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request, response);
        return ResponseEntity.ok().build();
    }

    /**
     * 리프레시 토큰을 사용하여 액세스 토큰 갱신 API
     * 필요한 경우 리프레시 토큰도 함께 갱신
     */
    @PostMapping("/auth/refresh")
    public ResponseEntity<LoginResDto> refreshToken(@CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        LoginResDto loginResDto = authService.refreshToken(refreshToken, response);
        return ResponseEntity.ok(loginResDto);
    }

    /**
     * 액세스 토큰이 만료되었을 때 사용하는 토큰 갱신 API
     * 만료된 액세스 토큰에서 정보를 추출하여 새 토큰 발급
     */
    @PostMapping("/auth/token/refresh")
    public ResponseEntity<LoginResDto> refreshTokenWithAccessToken(@CookieValue(name = "accessToken", required = false) String accessToken,
            @CookieValue(name = "refreshToken", required = false) String refreshToken, HttpServletResponse response) {

        LoginResDto loginResDto = authService.refreshTokenWithAccessToken(accessToken, refreshToken, response);
        return ResponseEntity.ok(loginResDto);
    }
}
