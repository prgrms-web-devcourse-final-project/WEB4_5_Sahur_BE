package com.team5.backend.global.security;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import com.team5.backend.global.oauth2.GoogleUserInfo;
import com.team5.backend.global.oauth2.KakaoUserInfo;
import com.team5.backend.global.oauth2.NaverUserInfo;
import com.team5.backend.global.oauth2.OAuth2UserInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private static final BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    private final MemberRepository memberRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);
        OAuth2UserInfo oAuth2UserInfo = null;

        // 액세스 토큰 얻기
        String accessToken = userRequest.getAccessToken().getTokenValue();
        System.out.println("Access Token: " + accessToken);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        switch (registrationId) {
            case "google":
                oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
                break;
            case "naver":
                oAuth2UserInfo = new NaverUserInfo((Map) oAuth2User.getAttributes().get("response"));
                break;
            case "kakao":
                oAuth2UserInfo = new KakaoUserInfo(oAuth2User.getAttributes());
                break;
            default:
                log.info("error");
                break;
        }

        String randomPassword = generateRandomPassword(15);  // 15자리 랜덤 비밀번호 생성

        String provider = oAuth2UserInfo.getProvider();
        String providerId = oAuth2UserInfo.getProviderId();
        String username = provider + "_" + providerId;
        String password = bCryptPasswordEncoder.encode(randomPassword);
        String email = oAuth2UserInfo.getEmail();
        String imageUrl = oAuth2UserInfo.getImageUrl();

        // 사용자 조회
        Optional<Member> memberEntityOptional = memberRepository.findByEmail(email);

        if (memberEntityOptional.isEmpty()) {

            System.out.println("OAuth 로그인이 최초입니다.");

            // 새로운 Member 객체 생성
            Member memberEntity = Member.builder()
                    .email(email)
                    .nickname(username) // 프로바이더_ID를 기본 닉네임으로 설정
                    .name(oAuth2UserInfo.getName() != null ? oAuth2UserInfo.getName() : username) // 이름 정보가 있으면 사용, 없으면 username 사용
                    .password(password)
                    .deleted(false)
                    .address(null) // 기본 주소는 빈 문자열로 설정 (나중에 사용자가 업데이트할 수 있도록)
                    .imageUrl(imageUrl) // 프로필 이미지 가져와서 설정
                    .role(Role.USER)
                    .emailVerified(true) // OAuth 로그인은 이메일이 검증된 것으로 간주
                    .build();

            // 새로운 회원 DB에 저장
            memberRepository.save(memberEntity);

            return new PrincipalDetails(memberEntity, oAuth2User.getAttributes());
        } else {
            System.out.println("로그인을 이미 한 적이 있습니다. 당신은 자동 회원가입이 되어 있습니다.");
            // 이미 존재하는 사용자라면 기본 정보를 기반으로 로그인 처리
        }

        // PrincipalDetails 반환
        return new PrincipalDetails(memberEntityOptional.orElseThrow(() -> new OAuth2AuthenticationException("Member not found")),
                oAuth2User.getAttributes());
    }

    // 랜덤 비밀번호 생성
    private static String generateRandomPassword(int length) {

        StringBuilder password = new StringBuilder(length);
        String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < length; i++) {

            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
