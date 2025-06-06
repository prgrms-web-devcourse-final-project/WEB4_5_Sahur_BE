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
import com.team5.backend.global.security.PrincipalDetails;
import com.team5.backend.global.util.ImageType;
import com.team5.backend.global.util.ImageUtil;
import com.team5.backend.global.util.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

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
    private final JwtUtil jwtUtil;

    private final HistoryRepository historyRepository;

    // 회원 생성
    @Transactional
    public SignupResDto signup(SignupReqDto signupReqDto, MultipartFile profileImage) throws IOException {

        // SignupReqDto 가 null로 요청이 오면 예외 발생
        if (signupReqDto == null) {
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }

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
            imageUrl = imageUtil.saveImage(profileImage, ImageType.PROFILE);
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
                .phoneNumber(signupReqDto.getPhoneNumber())
                .deleted(false)
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
    public PatchMemberResDto updateMember(Long memberId, PatchMemberReqDto patchMemberReqDto, MultipartFile newProfileImage) throws IOException {

        Member existingMember = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 모든 값들이 없이 요청이 온다면 예외 발생
        if (patchMemberReqDto == null && newProfileImage == null) {
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }

        // 이미지가 바뀌었을 때 patchMemberReqDto가 null이면 빈 객체 생성 (이미지만 변경 시)
        if (patchMemberReqDto == null && newProfileImage != null && !newProfileImage.isEmpty()) {
            patchMemberReqDto = new PatchMemberReqDto();
        }

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

        // 프로필 이미지 처리
        if (newProfileImage != null && !newProfileImage.isEmpty()) {
            // 기존 이미지 삭제 시도 (S3에 없어도 DB는 새 이미지로 업데이트됨)
            if (existingMember.getImageUrl() != null && !existingMember.getImageUrl().isBlank()) {
                boolean deleteSuccess = imageUtil.deleteImage(existingMember.getImageUrl());
                if (!deleteSuccess) {
                    log.info("S3에 파일이 없었지만 DB의 imageUrl은 새 이미지로 업데이트됩니다: {}", existingMember.getImageUrl());
                }
            }

            // 새 이미지 업로드 및 DTO에 반영
            String newImageUrl = imageUtil.saveImage(newProfileImage, ImageType.PROFILE);
            patchMemberReqDto.setImageUrl(newImageUrl);
        }

        // 변경할 필드만 수정
        existingMember.updateMember(patchMemberReqDto, passwordEncoder);

        return new PatchMemberResDto(existingMember.getMemberId(), "회원 정보가 성공적으로 수정되었습니다.");
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

            // Bearer 접두사 추가 (로그아웃 메서드에서 처리하므로)
            String headerToken = "Bearer " + token;

            // 로그아웃 메서드 호출
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            authService.logout(headerToken, request, response);

        } catch (CustomException e) {
            log.info("회원 탈퇴 중 인증 관련 오류 발생: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.info("회원 탈퇴 중 예상치 못한 오류 발생", e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }

        // 소프트 딜리트 적용
        member.softDelete();
        memberRepository.save(member);

        log.info("회원 ID {} 소프트 딜리트 처리 완료. 30일 후 영구 삭제 예정", memberId);
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
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(MemberErrorCode.EMAIL_NOT_VERIFIED));

        // 비밀번호 암호화 및 업데이트
        member.updatePassword(newPassword, passwordEncoder);

        // Redis에 저장된 인증 정보 삭제
        mailService.clearPasswordResetVerificationStatus(email);

        return PasswordResetResDto.builder()
                .success(true)
                .message("비밀번호가 성공적으로 재설정되었습니다.")
                .build();
    }

    public NicknameCheckResDto checkNicknameDuplicate(String nickname) {

        boolean exists = memberRepository.existsByNickname(nickname);
        if (exists) throw new CustomException(MemberErrorCode.NICKNAME_ALREADY_USED);

        return NicknameCheckResDto.builder()
                .exists(exists)
                .build();
    }

    @Transactional
    public MemberRestoreResDto restoreMember(Long memberId, HttpServletResponse response) {

        // 삭제된 회원을 포함하여 조회
        Member member = memberRepository.findByIdAllMembers(memberId)
                .orElseThrow(() -> new CustomException(MemberErrorCode.MEMBER_NOT_FOUND));

        // 회원이 삭제된 상태인지 확인
        if (!member.getDeleted()) {
            throw new CustomException(MemberErrorCode.MEMBER_NOT_DELETED);
        }

        // 회원 복구 처리
        member.restore();
        memberRepository.save(member);

        log.info("회원 ID {} 복구 처리 완료", member.getMemberId());

        return authService.handleMemberRestoreAuth(member, response);
    }

    // 회원 탈퇴 30일 후 하드 딜리트 진행(매일 자정 수행)
    @Scheduled(cron = "0 0 0 * * ?")
    @Transactional
    public void hardDeleteMembers() {

        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

        // 삭제 대상 회원 조회
        List<Member> deletedMembers = memberRepository.findAllDeletedMembers(thirtyDaysAgo);

        for (Member member : deletedMembers) {
            String imageUrl = member.getImageUrl(); // 프로필 이미지 URL 가져오기
            try {
                imageUtil.deleteImage(imageUrl); // S3에서 이미지 삭제
            } catch (IOException e) {
                log.error("S3 프로필 이미지 삭제 실패 (memberId={}): {}", member.getMemberId(), e.getMessage());
            }
        }

        // 회원 하드 딜리트
        int deletedCount = memberRepository.hardDeleteByDeletedAt(thirtyDaysAgo);
        log.info("삭제된 회원 수: {}", deletedCount);
    }

    public GetMemberResDto getCurrentMemberInfo(PrincipalDetails userDetails) {

        // 로그인 되지 않은 경우
        if (userDetails == null) {
            return GetMemberResDto.builder()
                    .isLoggedIn(false)
                    .build();
        }

        // 로그인된 경우
        Long memberId = userDetails.getMember().getMemberId();
        return getMemberById(memberId);
    }
}
