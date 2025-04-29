package com.team5.backend.domain.history.repository;

import com.team5.backend.domain.history.entity.History;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoryRepository extends JpaRepository<History, Long> {
    Page<History> findByMemberId(Long memberId, Pageable pageable);
}
