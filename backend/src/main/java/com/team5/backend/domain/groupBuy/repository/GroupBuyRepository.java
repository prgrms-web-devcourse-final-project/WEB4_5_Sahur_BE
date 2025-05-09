package com.team5.backend.domain.groupBuy.repository;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import com.team5.backend.domain.groupBuy.entity.GroupBuyStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {

    // 마감일(deadline)이 오늘인 GroupBuy 가져오기
    Page<GroupBuy> findByDeadlineBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
    List<GroupBuy> findByStatus(GroupBuyStatus status);
    Page<GroupBuy> findByGroupBuyIdIn(List<Long> groupBuyIds, Pageable pageable);
    Page<GroupBuy> findByStatus(GroupBuyStatus status, Pageable pageable);
    @Query("SELECT g FROM GroupBuy g WHERE g.status = 'ONGOING' ORDER BY g.product.dibCount DESC")
    List<GroupBuy> findTop3ByDibsOrder(Pageable pageable);
    @Query(value = """
        SELECT * FROM group_buy g
        JOIN product p ON g.product_id = p.product_id
        WHERE p.category_id = :categoryId
        AND g.status = 'ONGOING'
        ORDER BY RAND()
        LIMIT 3
    """, nativeQuery = true)
    List<GroupBuy> findRandomTop3ByCategoryId(@Param("categoryId") Long categoryId);

}
