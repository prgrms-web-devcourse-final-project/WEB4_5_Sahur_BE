package com.team5.backend.global.init;

import com.team5.backend.domain.member.member.entity.Member;
import com.team5.backend.domain.member.member.entity.Role;
import com.team5.backend.domain.member.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Transactional
public class BaseInitData implements CommandLineRunner {

//************로그인 테스트용 임시 클래스, 클래스명 필요하시분 수정가능합니다************

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {

        initMembers();
    }

    private void initMembers() {

        if (memberRepository.count() == 0) {

            Member user1 = Member.builder()
                    .name("테스트")
                    .email("example@example.com")
                    .password(passwordEncoder.encode("password1!"))
                    .nickname("테스트")
                    .address("서울")
                    .role(Role.USER)
                    .emailVerified(true)
                    .imageUrl("http://example.com/image.jpg")
                    .build();
            memberRepository.save(user1);
        }
    }
}