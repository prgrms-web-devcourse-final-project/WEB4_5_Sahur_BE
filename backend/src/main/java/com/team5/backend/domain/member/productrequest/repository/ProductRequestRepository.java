package com.team5.backend.domain.member.productrequest.repository;

import com.team5.backend.domain.member.productrequest.entity.ProductRequest;
import com.team5.backend.domain.member.productrequest.entity.ProductRequestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRequestRepository extends JpaRepository<ProductRequest, Long> {
    Page<ProductRequest> findAllByStatus(ProductRequestStatus status, Pageable pageable);
    Page<ProductRequest> findByMemberMemberId(Long memberId, Pageable pageable);

}
