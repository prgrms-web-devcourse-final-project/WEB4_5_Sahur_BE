package com.team5.backend.domain.member.member.service;


import com.team5.backend.domain.member.member.dto.EmailResDto;
import com.team5.backend.domain.member.member.dto.EmailSendReqDto;
import com.team5.backend.domain.member.member.dto.EmailVerificationReqDto;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.exception.CustomException;
import com.team5.backend.global.exception.code.CommonErrorCode;
import com.team5.backend.global.exception.code.MemberErrorCode;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final MemberRepository memberRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.mail.properties.auth-code-expiration-millis}")
    private long authCodeExpirationMillis;

    @Value("${email.auth.expiration.seconds}")
    private long authCodeExpirationSeconds;

    @Value("${email.verified.expiration.minutes}")
    private long verifiedExpirationMinutes;

    // Redis에 키 저장 시 접두어
    private static final String EMAIL_AUTH_PREFIX = "EMAIL_AUTH:";

    // 이메일 인증 성공 정보 저장 필드
    private static final String EMAIL_VERIFIED_PREFIX = "EMAIL_VERIFIED:";

    // 비밀번호 재설정 인증 코드 저장 필드
    private static final String PASSWORD_RESET_AUTH_PREFIX = "PASSWORD_RESET:";

    // 비밀번호 재설정 성공 정보 저장 필드
    private static final String PASSWORD_RESET_VERIFIED_PREFIX = "PASSWORD_RESET_VERIFIED:";

    public String createCode() {

        Random random = new Random();
        StringBuilder key = new StringBuilder();

        for (int i = 0; i < 6; i++) { // 인증 코드 6자리
            int index = random.nextInt(2); // 0~1까지 랜덤, 랜덤값으로 switch문 실행

            switch (index) {
                case 0 -> key.append((char) (random.nextInt(26) + 65)); // 대문자
                case 1 -> key.append(random.nextInt(10)); // 숫자
            }
        }
        return key.toString();
    }

    public MimeMessage createMail(String mail, String authCode) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("이메일 인증");
        String body = "";
        body += "<h3>요청하신 인증 번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 비밀번호 재설정을 위한 메일 생성
    public MimeMessage createPasswordResetMail(String mail, String authCode) throws MessagingException {

        MimeMessage message = javaMailSender.createMimeMessage();

        message.setFrom(senderEmail);
        message.setRecipients(MimeMessage.RecipientType.TO, mail);
        message.setSubject("비밀번호 재설정 인증");
        String body = "";
        body += "<h3>비밀번호 재설정 인증번호입니다.</h3>";
        body += "<h1>" + authCode + "</h1>";
        body += "<h3>감사합니다.</h3>";
        message.setText(body, "UTF-8", "html");

        return message;
    }

    // 메일 발송
    public String sendSimpleMessage(String sendEmail) {

        try {
            String authCode = createCode(); // 랜덤 인증번호 생성
            MimeMessage message = createMail(sendEmail, authCode); // 메일 생성
            javaMailSender.send(message); // 메일 발송

            return authCode;
        } catch (MailException e) {

            log.error("메일 발송 실패: " + sendEmail, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        } catch (MessagingException e) {

            log.error("메일 메시지 생성 실패: " + sendEmail, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }
    }

    @Transactional
    public EmailResDto sendAuthCode(EmailSendReqDto emailSendReqDto) {

        String email = emailSendReqDto.getEmail();

        try {

            // 이메일 중복 확인
            if (memberRepository.existsByEmail(email)) {
                throw new CustomException(MemberErrorCode.EMAIL_ALREADY_USED);
            }

            // 기존에 저장된 인증 코드가 있으면 삭제
            String key = EMAIL_AUTH_PREFIX + email;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);
            }

            // 새로운 인증 코드 발송
            String authCode = sendSimpleMessage(email);

            // Redis에 인증 코드 저장(만료 시간 설정)
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(key, authCode);
            redisTemplate.expire(key, authCodeExpirationSeconds, TimeUnit.SECONDS); // 3분

            return EmailResDto.builder()
                    .success(true)
                    .message("인증 코드가 전송되었습니다.")
                    .build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {

            log.error("인증 코드 전송 중 오류 발생: " + email, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }
    }

    public EmailResDto validationAuthCode(EmailVerificationReqDto emailVerificationReqDto) {

        String email = emailVerificationReqDto.getEmail();
        String authCode = emailVerificationReqDto.getAuthCode();

        try {

            // 이메일 중복 확인
            if (memberRepository.existsByEmail(email)) {
                throw new CustomException(MemberErrorCode.EMAIL_ALREADY_USED);
            }

            // Redis에서 인증 코드 조회
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            String key = EMAIL_AUTH_PREFIX + email;
            String storedAuthCode = values.get(key);

            // 인증 코드 유효성 검증
            if (storedAuthCode == null) {
                throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
            }

            // 인증 코드 일치 검증
            if (!storedAuthCode.equals(authCode)) {
                throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
            }

            // 인증 성공 시 Redis에 인증 완료 상태 저장
            String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
            values.set(verifiedKey, "true");
            redisTemplate.expire(verifiedKey, verifiedExpirationMinutes, TimeUnit.MINUTES); // 3분

            // 인증 성공 후 Redis에서 인증 코드 삭제
            redisTemplate.delete(key);

            return EmailResDto.builder()
                    .success(true)
                    .message("이메일 인증에 성공하였습니다.")
                    .build();
        } catch (CustomException e) {
            throw e;
        } catch (Exception e) {

            log.error("인증 코드 검증 중 오류 발생: " + email, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }
    }

    @Transactional
    public EmailResDto sendPasswordResetAuthCode(EmailSendReqDto emailSendReqDto) {

        String email = emailSendReqDto.getEmail();

        try {

            // 이메일이 존재하는지 확인
            if (!memberRepository.existsByEmail(email)) {
                throw new CustomException(MemberErrorCode.MEMBER_NOT_FOUND);
            }

            // 기존에 저장된 인증 코드가 있으면 삭제
            String key = PASSWORD_RESET_AUTH_PREFIX + email;
            if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
                redisTemplate.delete(key);
            }

            // 새로운 인증 코드 생성
            String authCode = createCode();

            // 인증코드가 있는 메일 생성 및 발송
            MimeMessage message = createPasswordResetMail(email, authCode);
            javaMailSender.send(message);

            // Redis에 인증 코드 저장(만료 시간 설정)
            ValueOperations<String, String> values = redisTemplate.opsForValue();
            values.set(key, authCode);
            redisTemplate.expire(key, authCodeExpirationSeconds, TimeUnit.SECONDS); // 3분

            return EmailResDto.builder()
                    .success(true)
                    .message("비밀번호 재설정 인증번호가 이메일로 전송되었습니다.")
                    .build();
        } catch (CustomException e) {
            throw e;
        } catch (MessagingException e) {

            log.error("비밀번호 재설정 메일 생성 실패: " + email, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        } catch (MailException e) {

            log.error("비밀번호 재설정 메일 발송 실패: " + email, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        } catch (Exception e) {

            log.error("비밀번호 재설정 인증코드 처리 중 오류 발생: " + email, e);
            throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
        }
    }

    // 비밀번호 재설정 코드 검증
    public EmailResDto verifyPasswordResetAuthCode(EmailVerificationReqDto emailVerificationReqDto) {

        String email = emailVerificationReqDto.getEmail();
        String authCode = emailVerificationReqDto.getAuthCode();

        // Redis에서 인증 코드 조회
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String key = PASSWORD_RESET_AUTH_PREFIX + email;
        String storedAuthCode = values.get(key);

        // 인증 코드 검증
        if (storedAuthCode != null && storedAuthCode.equals(authCode)) {

            // 인증 성공 시 Redis에 인증 완료 상태 저장
            String verifiedKey = PASSWORD_RESET_VERIFIED_PREFIX + email;
            values.set(verifiedKey, "true");
            redisTemplate.expire(verifiedKey, verifiedExpirationMinutes, TimeUnit.MINUTES); // 3분

            // 인증 코드 삭제
            redisTemplate.delete(key);

            return EmailResDto.builder()
                    .success(true)
                    .message("인증이 완료되었습니다. 새 비밀번호를 설정하세요.")
                    .build();
        }

        throw new CustomException(CommonErrorCode.VALIDATION_ERROR);
    }

    // 회원가입 용 인증 상태 확인
    public boolean isEmailVerified(String email) {

        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
        String verified = values.get(verifiedKey);

        return verified != null && verified.equals("true");
    }

    // 비밀번호 재설정 용 인증 상태 확인
    public boolean isPasswordResetVerified(String email) {

        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String verifiedKey = PASSWORD_RESET_VERIFIED_PREFIX + email;
        String verified = values.get(verifiedKey);

        return verified != null && verified.equals("true");
    }

    // 인증 상태 삭제 (회원가입 후 사용)
    public void clearEmailVerificationStatus(String email) {

        String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
        redisTemplate.delete(verifiedKey);
    }

    // 인증 상태 삭제 (비밀번호 재설정 후 사용)
    public void clearPasswordResetVerificationStatus(String email) {

        String verifiedKey = PASSWORD_RESET_VERIFIED_PREFIX + email;
        redisTemplate.delete(verifiedKey);
    }
}