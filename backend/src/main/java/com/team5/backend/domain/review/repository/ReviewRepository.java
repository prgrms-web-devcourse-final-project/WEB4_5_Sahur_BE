package com.team5.backend.domain.review.repository;

import com.team5.backend.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProduct_ProductId(Long productId, Pageable pageable);
    Page<Review> findByMember_MemberId(Long memberId, Pageable pageable);
}
