package com.team5.backend.domain.dibs.repository;

import com.team5.backend.domain.dibs.entity.Dibs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DibsRepository extends JpaRepository<Dibs, Long> {

    Page<Dibs> findByMember_MemberId(Long memberId, Pageable pageable);

    List<Dibs> findByMember_MemberId(Long memberId);

    Optional<Dibs> findByProduct_ProductIdAndMember_MemberId(Long productId, Long memberId);

}
