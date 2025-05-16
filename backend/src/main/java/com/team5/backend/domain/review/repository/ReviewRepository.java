package com.team5.backend.domain.review.repository;

import com.team5.backend.domain.review.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Page<Review> findByProductProductId(Long productId, Pageable pageable);
    Page<Review> findByMemberMemberId(Long memberId, Pageable pageable);
    @Query("SELECT AVG(r.rate) FROM Review r WHERE r.product.productId = :productId")
    Double findAverageRatingByProductId(@Param("productId") Long productId);
    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.productId = :productId")
    Integer countByProductId(@Param("productId") Long productId);


}
