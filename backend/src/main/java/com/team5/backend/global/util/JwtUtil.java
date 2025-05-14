package com.team5.backend.global.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${custom.jwt.secret-key}")
    private String secretKey;

    @Value("${custom.jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${custom.jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    @Value("${custom.jwt.temporary-token-expiration}")
    private long temporaryTokenExpiration;

    private final RedisTemplate<String, String> redisTemplate;

    private static final String REDIS_ACCESS_TOKEN_PREFIX = "access:";
    private static final String REDIS_REFRESH_TOKEN_PREFIX = "refresh:";
    private static final String REDIS_BLACKLIST_PREFIX = "blacklist:";
    private static final String REDIS_REFRESH_BLACKLIST_PREFIX = "refresh_blacklist:";
    private static final String REDIS_REMEMBER_ME_PREFIX = "remember_me:";
    private static final long REMEMBER_ME_EXPIRATION = 1209600000;

    // 액세스 토큰 생성
    public String generateAccessToken(Long memberId, String email, String role) {

        String token = generateToken(memberId, email, role, accessTokenExpiration);

        // Redis에 액세스 토큰 저장
        redisTemplate.opsForValue().set(
                REDIS_ACCESS_TOKEN_PREFIX + email,
                token,
                accessTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    // 리프레시 토큰 생성 (필수 정보만 파라미터로 받음)
    public String generateRefreshToken(Long memberId, String email, String role) {

        String refreshToken = generateToken(memberId, email, role, refreshTokenExpiration);

        // Redis에 리프레시 토큰 저장
        redisTemplate.opsForValue().set(
                REDIS_REFRESH_TOKEN_PREFIX + email,
                refreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        return refreshToken;
    }

    // 공통 토큰 생성 메서드
    private String generateToken(Long memberId, String email, String role, long expirationTime) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", memberId);
        claims.put("role", role);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    // 액세스 토큰 블랙리스트에 추가 (로그아웃 시 사용)
    public void addToBlacklist(String token) {
        // 토큰의 남은 유효 시간 계산
        long expiration = getClaims(token).getExpiration().getTime();
        long now = System.currentTimeMillis();
        long ttl = expiration - now;

        if (ttl > 0) {
            // 블랙리스트에 토큰 추가 (만료 시간까지만 저장)
            redisTemplate.opsForValue().set(
                    REDIS_BLACKLIST_PREFIX + token,
                    "logout",
                    ttl,
                    TimeUnit.MILLISECONDS
            );

            // 사용자의 액세스 토큰도 Redis에서 삭제
            String email = extractEmail(token);
            redisTemplate.delete(REDIS_ACCESS_TOKEN_PREFIX + email);
        }
    }

    // 리프레시 토큰 블랙리스트에 추가
    public void addRefreshTokenToBlacklist(String refreshToken) {
        // 토큰의 남은 유효 시간 계산
        long expiration = getClaims(refreshToken).getExpiration().getTime();
        long now = System.currentTimeMillis();
        long ttl = expiration - now;

        if (ttl > 0) {
            // 블랙리스트에 리프레시 토큰 추가 (만료 시간까지만 저장)
            redisTemplate.opsForValue().set(
                    REDIS_REFRESH_BLACKLIST_PREFIX + refreshToken,
                    "logout",
                    ttl,
                    TimeUnit.MILLISECONDS
            );

            // 사용자의 리프레시 토큰도 Redis에서 삭제
            String email = extractEmail(refreshToken);
            redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + email);
        }
    }

    // Redis에서 리프레시 토큰 검증
    public boolean validateRefreshTokenInRedis(String email, String refreshToken) {
        String storedToken = redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + email);
        return refreshToken.equals(storedToken);
    }

    // Redis에서 액세스 토큰 검증
    public boolean validateAccessTokenInRedis(String email, String accessToken) {
        String storedToken = redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_PREFIX + email);
        return accessToken.equals(storedToken);
    }

    // 액세스 토큰이 블랙리스트에 있는지 확인
    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_BLACKLIST_PREFIX + token));
    }

    // 리프레시 토큰이 블랙리스트에 있는지 확인
    public boolean isRefreshTokenBlacklisted(String refreshToken) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(REDIS_REFRESH_BLACKLIST_PREFIX + refreshToken));
    }

    // 리프레시 토큰 삭제 메서드
    public void removeRefreshToken(String email) {
        // 기존 리프레시 토큰 조회
        String storedRefreshToken = getStoredRefreshToken(email);

        // 기존 토큰이 있다면 블랙리스트에 추가
        if (storedRefreshToken != null) {
            addRefreshTokenToBlacklist(storedRefreshToken);
        }

        // Redis에서 리프레시 토큰 키 삭제
        redisTemplate.delete(REDIS_REFRESH_TOKEN_PREFIX + email);
    }

    // 토큰에서 이메일 추출
    public String extractEmail(String token) {
        return getClaims(token).getSubject();
    }

    // 토큰에서 memberId 추출
    public Long extractMemberId(String token) {
        return getClaims(token).get("memberId", Long.class);
    }

    // 토큰에서 Role 추출
    public String extractRole(String token) {
        return getClaims(token).get("role", String.class);
    }

    public boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(new Date());
    }

    // 토큰 유효성 검증 (블랙리스트 확인 및 Redis 검증 추가)
    public boolean validateToken(String token, String email) {
        return (email.equals(extractEmail(token)) &&
                !isTokenExpired(token) &&
                !isTokenBlacklisted(token) &&
                validateAccessTokenInRedis(email, token));
    }

    public Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return new SecretKeySpec(secretKey.getBytes(), SignatureAlgorithm.HS512.getJcaName());
    }

    public long getAccessTokenExpiration() {
        return accessTokenExpiration;
    }

    public long getRefreshTokenExpiration() {
        return refreshTokenExpiration;
    }

    // 액세스 토큰 키 값 조회를 위한 메서드
    public String getStoredAccessToken(String email) {
        return redisTemplate.opsForValue().get(REDIS_ACCESS_TOKEN_PREFIX + email);
    }

    // 리프레시 토큰 키 값 조회를 위한 메서드
    public String getStoredRefreshToken(String email) {
        return redisTemplate.opsForValue().get(REDIS_REFRESH_TOKEN_PREFIX + email);
    }

    // 리프레시 토큰 업데이트
    public void updateRefreshTokenInRedis(String email, String newRefreshToken) {
        // 기존 리프레시 토큰 조회
        String oldRefreshToken = getStoredRefreshToken(email);

        // 기존 토큰이 있고 새 토큰과 다르다면 블랙리스트에 추가
        if (oldRefreshToken != null && !oldRefreshToken.equals(newRefreshToken)) {
            addRefreshTokenToBlacklist(oldRefreshToken);
        }

        // 새 리프레시 토큰 저장
        redisTemplate.opsForValue().set(
                REDIS_REFRESH_TOKEN_PREFIX + email,
                newRefreshToken,
                refreshTokenExpiration,
                TimeUnit.MILLISECONDS
        );
    }

    // Remember Me 토큰 생성
    public String generateRememberMeToken(Long memberId, String email, String role) {

        String token = generateToken(memberId, email, role, REMEMBER_ME_EXPIRATION);

        // Redis에 Remember Me 토큰 저장
        redisTemplate.opsForValue().set(
                REDIS_REMEMBER_ME_PREFIX + email,
                token,
                REMEMBER_ME_EXPIRATION,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    // Redis에서 Remember Me 토큰 검증
    public boolean validateRememberMeTokenInRedis(String email, String rememberMeToken) {

        String storedToken = redisTemplate.opsForValue().get(REDIS_REMEMBER_ME_PREFIX + email);
        return rememberMeToken.equals(storedToken);
    }

    // Remember Me 토큰 조회
    public String getStoredRememberMeToken(String email) {
        return redisTemplate.opsForValue().get(REDIS_REMEMBER_ME_PREFIX + email);
    }

    // Remember Me 토큰 블랙리스트에 추가
    public void invalidateRememberMeToken(String email) {

        String rememberMeToken = getStoredRememberMeToken(email);
        if (rememberMeToken != null) {
            redisTemplate.delete(REDIS_REMEMBER_ME_PREFIX + email);
        }
    }

    // Remember Me 만료 시간 가져오기
    public long getRememberMeExpiration() {
        return REMEMBER_ME_EXPIRATION;
    }

    // 탈퇴 회원용 임시 토큰 생성
    public String generateDeletedMemberToken(Long memberId, String email, String role) {

        // 토큰에 삭제된 회원임을 표시
        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", memberId);
        claims.put("role", role);
        claims.put("isDeleted", true);  // 삭제된 회원 표시

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + temporaryTokenExpiration);

        String token = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .addClaims(claims)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();

        // Redis에 임시 토큰 저장 (다른 접두사 사용)
        redisTemplate.opsForValue().set(
                "deleted_member:" + email,
                token,
                temporaryTokenExpiration,
                TimeUnit.MILLISECONDS
        );

        return token;
    }

    // 토큰이 삭제된 회원용인지 확인
    public boolean isDeletedMemberToken(String token) {
        try {
            Claims claims = getClaims(token);
            return claims.get("isDeleted", Boolean.class) != null &&
                    claims.get("isDeleted", Boolean.class);
        } catch (Exception e) {
            return false;
        }
    }

    public long getTemporaryTokenExpiration() {
        return temporaryTokenExpiration;
    }
}