package com.team5.backend.domain.member.member.repository;

import com.team5.backend.domain.member.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    Optional<Member> findByEmail(String email);

    // 소프트 딜리트된 회원을 포함하여 회원 아이디로 회원 조회
    @Query("SELECT m FROM Member m WHERE m.memberId = :memberId")
    Optional<Member> findByIdAllMembers(@Param("memberId") Long memberId);

    // 소프트 딜리트된 회원을 포함하여 회원 이메일로 회원 조회
    @Query("SELECT m FROM Member m WHERE m.email = :email")
    Optional<Member> findByEmailAllMembers(@Param("email") String email);

    // 30일 이상 지난 탈퇴 회원 하드 딜리트
    @Modifying
    @Transactional
    @Query("DELETE FROM Member m WHERE m.deleted = true AND m.deletedAt < :deletedAt")
    int hardDeleteByDeletedAt(@Param("deletedAt") LocalDateTime deletedAt);

    @Query("SELECT m FROM Member m WHERE m.deleted = true AND m.deletedAt < :thirtyDaysAgo")
    List<Member> findAllDeletedMembers(@Param("thirtyDaysAgo") LocalDateTime thirtyDaysAgo);
}
