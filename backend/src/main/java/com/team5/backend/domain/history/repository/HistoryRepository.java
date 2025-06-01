package com.team5.backend.domain.history.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.history.entity.History;
import com.team5.backend.domain.product.entity.Product;

public interface HistoryRepository extends JpaRepository<History, Long> {
    /* 최근 내역이 있는 공동구매만 추출 + 최신순 정렬 */
    @Query(
            value = """
                    SELECT gb
                    FROM History h
                    JOIN h.groupBuy gb
                    WHERE h.member.memberId = :memberId
                    GROUP BY gb
                    ORDER BY MAX(h.createdAt) DESC
                    """,
            countQuery = """
                    SELECT COUNT(DISTINCT gb)
                    FROM History h
                    JOIN h.groupBuy gb
                    WHERE h.member.memberId = :memberId
                    """
    )
    Page<GroupBuy> findRecentGroupBuysByMember(@Param("memberId") Long memberId,
                                               Pageable pageable);


    @Query("select h.product from History h where h.member.memberId = :memberId and h.writable = true")
    Page<Product> findWritableProductsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    List<History> findByMember_MemberIdAndProduct_ProductId(Long memberId, Long productId);

    boolean existsByOrder_OrderId(Long orderId);

    void deleteByOrder_OrderId(Long orderId);

}
