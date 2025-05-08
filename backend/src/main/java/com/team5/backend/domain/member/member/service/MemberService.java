package com.team5.backend.domain.member.member.service;

import com.team5.backend.domain.history.repository.HistoryRepository;
import com.team5.backend.domain.member.member.dto.*;
import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.domain.product.dto.ProductResDto;
import com.team5.backend.global.entity.Address;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.AuthErrorCode;
import com.team5.backend.global.exception.code.CommonErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import com.team5.backend.global.security.AuthTokenManager;
import com.team5.backend.global.util.ImageUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final ImageUtil imageUtil;
    private final AuthTokenManager authTokenManager;

    private final HistoryRepository historyRepository;

    // 회원 생성
    @Transactional
    public SignupResDto signup(SignupReqDto signupReqDto, MultipartFile profileImage) throws IOException {
        String email = signupReqDto.getEmail();

        // 이메일 중복 검사
        if (memberRepository.existsByEmail(email)) {
            throw new CustomException(MemberErrorCode.EMAIL_ALREADY_USED);
        }

        // 닉네임 중복 검사
        if (memberRepository.existsByNickname(signupReqDto.getNickname())) {
            throw new CustomException(MemberErrorCode.NICKNAME_ALREADY_USED);
        }

        // 이메일 인증 상태 확인
        boolean isVerified = mailService.isEmailVerified(email);
        if (!isVerified) {
            throw new CustomException(MemberErrorCode.EMAIL_NOT_VERIFIED);
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(signupReqDto.getPassword());

        // 프로필 이미지 처리
        String imageUrl = "/images/default-profile.png"; // 기본값

        if (profileImage != null && !profileImage.isEmpty()) {
            // 이미지가 제공된 경우 업로드
            imageUrl = imageUtil.saveImage(profileImage);
            log.info("프로필 이미지 업로드: {}", imageUrl);
        } else {
            log.info("기본 이미지 설정");
        }

        Address address = signupReqDto.toAddress();

        Member member = Member.builder()
                .email(email)
                .nickname(signupReqDto.getNickname())
                .name(signupReqDto.getName())
                .password(encodedPassword)
                .address(address)
                .imageUrl(imageUrl)
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
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        return GetMemberResDto.fromEntity(member);
    }

    // 회원 정보 수정
    @Transactional
    public PatchMemberResDto updateMember(Long memberId, PatchMemberReqDto patchMemberReqDto) {

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 이메일 변경 시 중복 검사
        if (patchMemberReqDto.getEmail() != null && !patchMemberReqDto.getEmail().equals(existingMember.getEmail())
                && memberRepository.existsByEmail(patchMemberReqDto.getEmail())) {
            throw new CustomException(MemberErrorCode.EMAIL_ALREADY_USED);
        }

        // 닉네임 변경 시 중복 검사
        if (patchMemberReqDto.getNickname() != null && !patchMemberReqDto.getNickname().equals(existingMember.getNickname())
                && memberRepository.existsByNickname(patchMemberReqDto.getNickname())) {
            throw new CustomException(MemberErrorCode.NICKNAME_ALREADY_USED);
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

            // 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(patchMemberReqDto.getPassword());
            existingMember.setPassword(encodedPassword);
        }

//        if (patchMemberReqDto.getAddress() != null) {
//            existingMember.setAddress(patchMemberReqDto.getAddress());
//        }

        if (patchMemberReqDto.getImageUrl() != null) {
            existingMember.setImageUrl(patchMemberReqDto.getImageUrl());
        }

        Member updatedMember = memberRepository.save(existingMember);

        return new PatchMemberResDto(updatedMember.getMemberId(), "회원 정보가 성공적으로 수정되었습니다.");
    }

    // 회원 삭제
    @Transactional
    public void deleteMember(Long memberId, HttpServletResponse response) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        try {

            // 현재 사용자의 인증 정보 가져오기
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication == null) {
                throw new CustomException(CommonErrorCode.UNAUTHORIZED);
            }

            // 토큰 정보 추출
            String token = authTokenManager.extractToken(authentication);

            if (token == null) {
                throw new CustomException(AuthErrorCode.ACCESS_TOKEN_NOT_FOUND);
            }

            authService.logout(token, response);

        } catch (CustomException e) {
            log.error("회원 탈퇴 중 인증 관련 오류 발생: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("회원 탈퇴 중 예상치 못한 오류 발생", e);
            throw new CustomException(CommonErrorCode.INTERNAL_ERROR);
        }

        memberRepository.delete(member);
    }

    public Page<ProductResDto> getReviewableProductsByMember(Long memberId, Pageable pageable) {
        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), sort);
        return historyRepository.findWritableProductsByMemberId(memberId, sortedPageable)
                .map(ProductResDto::fromEntity);
    }

    /**
     * 비밀번호 재설정
     * TODO: 추가적인 검증 기능 필요(이메일을 알고있는 다른 유저가 비밀번호를 변경하려하는 경우 등)
     */
    @Transactional
    public PasswordResetResDto resetPassword(PasswordResetReqDto passwordResetReqDto) {

        String email = passwordResetReqDto.getEmail();
        String newPassword = passwordResetReqDto.getPassword();

        // 인증 완료 여부 조회
        if (!mailService.isPasswordResetVerified(email)) {
            return PasswordResetResDto.builder()
                    .success(false)
                    .message("이메일 인증이 완료되지 않았거나 인증 시간이 만료되었습니다.")
                    .build();
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 3비밀번호 암호화 및 업데이트
        String encodedPassword = passwordEncoder.encode(newPassword);
        member.setPassword(encodedPassword);
        memberRepository.save(member);

        // Redis에 저장된 인증 정보 삭제
        mailService.clearPasswordResetVerificationStatus(email);

        return PasswordResetResDto.builder()
                .success(true)
                .message("비밀번호가 성공적으로 재설정되었습니다.")
                .build();
    }

    public NicknameCheckResDto checkNicknameDuplicate(String nickname) {

        boolean exists = memberRepository.existsByNickname(nickname);

        return NicknameCheckResDto.builder()
                .exists(exists)
                .build();
    }
}
