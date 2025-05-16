package com.team5.backend.domain.history.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.product.entity.Product;

public interface HistoryRepository extends JpaRepository<History, Long> {
    @Query("SELECT DISTINCT h.groupBuy FROM History h WHERE h.member.memberId = :memberId")
    Page<GroupBuy> findDistinctGroupBuysByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    @Query("select h.product from History h where h.member.memberId = :memberId and h.writable = true")
    Page<Product> findWritableProductsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    History findByMember_MemberIdAndProduct_ProductId(Long memberId, Long productId);

    void deleteByOrder_OrderId(Long orderId);
}
