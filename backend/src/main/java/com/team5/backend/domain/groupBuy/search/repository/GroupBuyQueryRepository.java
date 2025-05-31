package com.team5.backend.domain.groupBuy.search.repository;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GroupBuyQueryRepository extends JpaRepository<GroupBuy, Long> {

    @Query("SELECT gb FROM GroupBuy gb " +
            "JOIN FETCH gb.product p " +
            "JOIN FETCH p.category c " +
            "WHERE LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<GroupBuy> searchByProductTitle(String keyword);
}