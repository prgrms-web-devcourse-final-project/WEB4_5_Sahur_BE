package com.team5.backend.domain.dibs.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.team5.backend.domain.dibs.entity.Dibs;

import io.lettuce.core.dynamic.annotation.Param;

@Repository
public interface DibsRepository extends JpaRepository<Dibs, Long> {

    Page<Dibs> findByMember_MemberId(Long memberId, Pageable pageable);

    List<Dibs> findByMember_MemberId(Long memberId);

    Optional<Dibs> findByProduct_ProductIdAndMember_MemberId(Long productId, Long memberId);

    @Query("SELECT d FROM Dibs d JOIN FETCH d.product WHERE d.member.memberId = :memberId")
    List<Dibs> findAllWithProductByMemberId(@Param("memberId") Long memberId);

    @Query(value = "SELECT d FROM Dibs d JOIN FETCH d.product WHERE d.member.memberId = :memberId",
            countQuery = "SELECT COUNT(d) FROM Dibs d WHERE d.member.memberId = :memberId")
    Page<Dibs> findPageWithProductByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("SELECT d.member.memberId FROM Dibs d WHERE d.product.productId = :productId")
    List<Long> findMemberIdsByProductId(@Param("productId") Long productId);
    
}
