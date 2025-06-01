package com.team5.backend.domain.groupBuy.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.team5.backend.domain.product.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "GroupBuy")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class GroupBuy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long groupBuyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    @Column
    private Integer targetParticipants = 0;

    @Column
    private Integer currentParticipantCount = 0;

    @Column
    private Integer round = 0;

    @Column(nullable = false)
    private LocalDateTime deadline = null;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GroupBuyStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    public void increaseParticipantCount(int amount) {
        this.currentParticipantCount += amount;
    }

    public void decreaseParticipantCount(int amount) {
        this.currentParticipantCount -= amount;
        if (this.currentParticipantCount < 0) {
            this.currentParticipantCount = 0;
        }
    }
}
