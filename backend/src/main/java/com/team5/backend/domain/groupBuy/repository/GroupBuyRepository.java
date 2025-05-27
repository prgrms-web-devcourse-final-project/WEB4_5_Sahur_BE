package com.team5.backend.domain.groupBuy.repository;

import com.team5.backend.domain.category.entity.CategoryType;
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
import java.util.Optional;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {

    // 마감일(deadline)이 오늘인 GroupBuy 가져오기
    Page<GroupBuy> findByDeadlineBetween(LocalDateTime startOfDay, LocalDateTime endOfDay, Pageable pageable);
    List<GroupBuy> findByStatus(GroupBuyStatus status);
    Page<GroupBuy> findByStatus(GroupBuyStatus status, Pageable pageable);
    @Query("SELECT g FROM GroupBuy g WHERE g.status = 'ONGOING' ORDER BY g.product.dibCount DESC")
    List<GroupBuy> findTop3ByDibsOrder(Pageable pageable);
    @Query("""
    SELECT g FROM GroupBuy g
    JOIN FETCH g.product p
    JOIN FETCH p.category c
    WHERE c.categoryType = :categoryType
    AND g.status = 'ONGOING'
""")
    List<GroupBuy> findAllByCategoryType(@Param("categoryType") CategoryType categoryType);


    @Query("""
    SELECT g FROM GroupBuy g
    JOIN g.product p
    WHERE p.category.categoryId = :categoryId
    AND g.status = 'ONGOING'
""")
    Page<GroupBuy> findOngoingByCategoryId(@Param("categoryId") Long categoryId, Pageable pageable);
    @Query("""
    SELECT g FROM GroupBuy g
    JOIN FETCH g.product p
    JOIN FETCH p.category
    WHERE g.groupBuyId = :groupBuyId
""")
    Optional<GroupBuy> findWithProductAndCategoryById(@Param("groupBuyId") Long groupBuyId);
    List<GroupBuy> findByProduct_ProductIdInAndStatus(List<Long> productIds, GroupBuyStatus status);





}
