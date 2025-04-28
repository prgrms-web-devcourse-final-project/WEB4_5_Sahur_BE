package com.team5.backend.domain.groupBuy.repository;

import com.team5.backend.domain.groupBuy.entity.GroupBuy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GroupBuyRepository extends JpaRepository<GroupBuy, Long> {
}
