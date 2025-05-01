package com.team5.backend.domain.history.repository;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.history.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT DISTINCT h.groupBuy FROM History h WHERE h.member.memberId = :memberId")
    Page<GroupBuy> findDistinctGroupBuysByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // writable = true 인 모든 기록
    List<History> findAllByMemberIdAndProductIdAndWritableTrue(Long memberId, Long productId);

    // writable = false 인 모든 기록
    List<History> findAllByMemberIdAndProductIdAndWritableFalse(Long memberId, Long productId);



}
