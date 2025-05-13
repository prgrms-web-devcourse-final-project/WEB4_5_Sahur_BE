package com.team5.backend.domain.member.member.repository;

import com.team5.backend.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    // 30일 이상 지난 탈퇴 회원 하드 딜리트
    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.deleted = true AND m.deletedAt < :deletedAt")
    int hardDeleteByDeletedAt(@Param("deletedAt") LocalDateTime deletedAt);
}
