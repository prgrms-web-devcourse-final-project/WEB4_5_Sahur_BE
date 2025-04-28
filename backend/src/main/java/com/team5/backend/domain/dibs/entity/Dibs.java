package com.team5.backend.domain.dibs.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "dibs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dibs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dibsId;

    @Column(nullable = false)
    private Long memberId;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean status;
}
