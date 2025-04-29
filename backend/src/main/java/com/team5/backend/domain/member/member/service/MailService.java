package com.team5.backend.domain.member.member.service;


import com.team5.backend.domain.member.member.dto.EmailVerificationRequestDto;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.concurrent.TimeUnit;

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

    // Redis에 키 저장 시 접두어
    private static final String EMAIL_AUTH_PREFIX = "EMAIL_AUTH:";

    // 이메일 인증 성공 정보 저장 필드
    private static final String EMAIL_VERIFIED_PREFIX = "EMAIL_VERIFIED:";

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

    // 메일 발송
    public String sendSimpleMessage(String sendEmail) throws MessagingException {

        String authCode = createCode(); // 랜덤 인증번호 생성

        MimeMessage message = createMail(sendEmail, authCode); // 메일 생성
        try {
            javaMailSender.send(message); // 메일 발송
            return authCode;
        } catch (MailException e) {
            return null;
        }
    }

    @Transactional
    public boolean sendAuthCode(String email) throws MessagingException {

        // 기존에 저장된 인증 코드가 있으면 삭제
        String key = EMAIL_AUTH_PREFIX + email;

        if (Boolean.TRUE.equals(redisTemplate.hasKey(key))) {
            redisTemplate.delete(key);
        }

        // 새로운 인증 코드 발송
        String authCode = sendSimpleMessage(email);

        if (authCode != null) {
            // Redis에 인증 코드 저장 (만료 시간 설정)
            ValueOperations<String, String> values = redisTemplate.opsForValue();

            // Redis에 저장하고 만료 시간 설정 (밀리초를 초 단위로 변환)
            values.set(key, authCode);
            redisTemplate.expire(key, 180, TimeUnit.SECONDS); // 3분

            return true;
        }
        return false;
    }

    public boolean validationAuthCode(EmailVerificationRequestDto emailVerificationRequestDto) {

        String email = emailVerificationRequestDto.getEmail();
        String authCode = emailVerificationRequestDto.getAuthCode();

        // Redis에서 인증 코드 조회
        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String key = EMAIL_AUTH_PREFIX + email;
        String storedAuthCode = values.get(key);

        // 인증 코드 검증
        if (storedAuthCode != null && storedAuthCode.equals(authCode)) {

            // 인증 성공 시 Redis에 인증 완료 상태 저장 (유효기간 10분)
            String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
            values.set(verifiedKey, "true");
            redisTemplate.expire(verifiedKey, 10, TimeUnit.MINUTES);

            // 인증 성공 후 Redis에서 인증 코드 삭제
            redisTemplate.delete(key);

            return true;
        }

        return false;
    }

    // 인증 상태 확인
    public boolean isEmailVerified(String email) {

        ValueOperations<String, String> values = redisTemplate.opsForValue();
        String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
        String verified = values.get(verifiedKey);

        return verified != null && verified.equals("true");
    }

    // 인증 상태 삭제 (회원가입 후 사용)
    public void clearEmailVerificationStatus(String email) {

        String verifiedKey = EMAIL_VERIFIED_PREFIX + email;
        redisTemplate.delete(verifiedKey);
    }
}
