package com.team5.backend.domain.member.service;

import com.team5.backend.domain.member.dto.GetMemberResDto;
import com.team5.backend.domain.member.dto.SignupReqDto;
import com.team5.backend.domain.member.dto.SignupResDto;
import com.team5.backend.domain.member.dto.UpdateMemberReqDto;
import com.team5.backend.domain.member.entity.Member;
import com.team5.backend.domain.member.entity.Role;
import com.team5.backend.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    // 회원 생성
    @Transactional
    public SignupResDto signup(SignupReqDto signupReqDto) {

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(signupReqDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(signupReqDto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        Member member = Member.builder()
                .email(signupReqDto.getEmail())
                .nickname(signupReqDto.getNickname())
                .name(signupReqDto.getName())
                .password(signupReqDto.getPassword())
                .address(signupReqDto.getAddress())
                .imageUrl(signupReqDto.getImageUrl())
                .role(Role.USER)
                .build();

        Member savedMember = memberRepository.save(member);

        return SignupResDto.builder()
                .memberId(savedMember.getId())
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
    public GetMemberResDto updateMember(Long memberId, UpdateMemberReqDto updateMemberReqDto) {
        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원을 찾을 수 없습니다. ID: " + memberId));

        // 이메일 변경 시 중복 검사
        if (updateMemberReqDto.getEmail() != null && !updateMemberReqDto.getEmail().equals(existingMember.getEmail())
                && memberRepository.existsByEmail(updateMemberReqDto.getEmail())) {
            throw new RuntimeException("이미 사용 중인 이메일입니다.");
        }

        // 닉네임 변경 시 중복 검사
        if (updateMemberReqDto.getNickname() != null && !updateMemberReqDto.getNickname().equals(existingMember.getNickname())
                && memberRepository.existsByNickname(updateMemberReqDto.getNickname())) {
            throw new RuntimeException("이미 사용 중인 닉네임입니다.");
        }

        // 변경할 필드만 수정
        if (updateMemberReqDto.getEmail() != null) {
            existingMember.setEmail(updateMemberReqDto.getEmail());
        }

        if (updateMemberReqDto.getNickname() != null) {
            existingMember.setNickname(updateMemberReqDto.getNickname());
        }

        if (updateMemberReqDto.getName() != null) {
            existingMember.setName(updateMemberReqDto.getName());
        }

        if (updateMemberReqDto.getPassword() != null) {
            existingMember.setPassword(updateMemberReqDto.getPassword());
        }

        if (updateMemberReqDto.getAddress() != null) {
            existingMember.setAddress(updateMemberReqDto.getAddress());
        }

        if (updateMemberReqDto.getImageUrl() != null) {
            existingMember.setImageUrl(updateMemberReqDto.getImageUrl());
        }

        Member updatedMember = memberRepository.save(existingMember);

        return GetMemberResDto.fromEntity(updatedMember);
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long memberId) {

        if (!memberRepository.existsById(memberId)) {
            throw new RuntimeException("회원을 찾을 수 없습니다. ID: " + memberId);
        }
        memberRepository.deleteById(memberId);
    }
}
