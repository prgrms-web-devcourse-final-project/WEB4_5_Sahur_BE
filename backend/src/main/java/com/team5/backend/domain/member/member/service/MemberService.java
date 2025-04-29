package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.member.member.dto.GetMemberResDto;
import com.team5.backend.domain.member.member.dto.SignupReqDto;
import com.team5.backend.domain.member.member.dto.SignupResDto;
import com.team5.backend.domain.member.member.dto.PatchMemberReqDto;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;

    // 회원 생성
    @Transactional
    public SignupResDto signup(SignupReqDto signupReqDto) {

        String email = signupReqDto.getEmail();

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(email)) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(signupReqDto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // 이메일 인증 상태 확인
        boolean isVerified = mailService.isEmailVerified(email);
        if (!isVerified) {
            throw new RuntimeException("이메일 인증이 완료되지 않았습니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupReqDto.getPassword());

        Member member = Member.builder()
                .email(email)
                .nickname(signupReqDto.getNickname())
                .name(signupReqDto.getName())
                .password(encodedPassword)
                .address(signupReqDto.getAddress())
                .imageUrl(signupReqDto.getImageUrl())
                .role(Role.USER)
                .emailVerified(true)  // 이미 인증이 완료된 상태이므로 true로 설정
                .build();

        Member savedMember = memberRepository.save(member);

        // 인증 상태 정보 삭제 (더 이상 필요 없음)
        mailService.clearEmailVerificationStatus(email);

        return SignupResDto.builder()
                .memberId(savedMember.getMemberId())
                .message("회원가입이 성공적으로 완료되었습니다.")
                .build();
    }

    // 회원 조회
    public GetMemberResDto getMemberById(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + memberId));

        return GetMemberResDto.fromEntity(member);
    }

    // 모든 회원 조회
    public List<GetMemberResDto> getAllMembers() {

        return memberRepository.findAll().stream()
                .map(GetMemberResDto::fromEntity)
                .collect(Collectors.toList());
    }

    // 회원 정보 수정
    @Transactional
    public GetMemberResDto updateMember(Long memberId, PatchMemberReqDto patchMemberReqDto) {

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + memberId));

        // 이메일 변경 시 중복 검사
        if (patchMemberReqDto.getEmail() != null && !patchMemberReqDto.getEmail().equals(existingMember.getEmail())
                && memberRepository.existsByEmail(patchMemberReqDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 변경 시 중복 검사
        if (patchMemberReqDto.getNickname() != null && !patchMemberReqDto.getNickname().equals(existingMember.getNickname())
                && memberRepository.existsByNickname(patchMemberReqDto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // 변경할 필드만 수정
        if (patchMemberReqDto.getEmail() != null) {
            existingMember.setEmail(patchMemberReqDto.getEmail());
        }

        if (patchMemberReqDto.getNickname() != null) {
            existingMember.setNickname(patchMemberReqDto.getNickname());
        }

        if (patchMemberReqDto.getName() != null) {
            existingMember.setName(patchMemberReqDto.getName());
        }

        if (patchMemberReqDto.getPassword() != null) {
            existingMember.setPassword(patchMemberReqDto.getPassword());
        }

        if (patchMemberReqDto.getAddress() != null) {
            existingMember.setAddress(patchMemberReqDto.getAddress());
        }

        if (patchMemberReqDto.getImageUrl() != null) {
            existingMember.setImageUrl(patchMemberReqDto.getImageUrl());
        }

        Member updatedMember = memberRepository.save(existingMember);

        return GetMemberResDto.fromEntity(updatedMember);
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long memberId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + memberId));

        memberRepository.delete(member);
    }
}
