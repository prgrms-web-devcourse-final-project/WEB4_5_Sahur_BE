package com.team5.backend.domain.member.controller;

import com.team5.backend.domain.member.dto.GetMemberResDto;
import com.team5.backend.domain.member.dto.SignupReqDto;
import com.team5.backend.domain.member.dto.SignupResDto;
import com.team5.backend.domain.member.dto.UpdateMemberReqDto;
import com.team5.backend.domain.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/member")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원 생성
    @PostMapping("/auth/signup")
    public ResponseEntity<SignupResDto> signup(@Valid @RequestBody SignupReqDto signupReqDto) {

        SignupResDto signupResDto = memberService.signup(signupReqDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(signupResDto);
    }

    // 회원 조회
    @GetMapping("/{memberId}")
    public ResponseEntity<GetMemberResDto> getMember(@PathVariable Long memberId) {

        GetMemberResDto memberResDto = memberService.getMemberById(memberId);
        return ResponseEntity.status(HttpStatus.OK).body(memberResDto);
    }

    // 회원 전체 조회
    @GetMapping
    public ResponseEntity<List<GetMemberResDto>> getAllMembers() {

        List<GetMemberResDto> members = memberService.getAllMembers();
        return ResponseEntity.status(HttpStatus.OK).body(members);
    }

    @PutMapping("/{memberId}")
    public ResponseEntity<GetMemberResDto> updateMember(@PathVariable Long memberId, @Valid @RequestBody UpdateMemberReqDto updateMemberReqDto) {

        GetMemberResDto updatedMember = memberService.updateMember(memberId, updateMemberReqDto);
        return ResponseEntity.ok(updatedMember);
    }

    // 회원 탈퇴
    @DeleteMapping("/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long memberId) {

        memberService.deleteMember(memberId);
        return ResponseEntity.noContent().build();
    }
}
