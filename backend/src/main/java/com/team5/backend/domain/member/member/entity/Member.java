package com.team5.backend.domain.member.member.entity;

import com.team5.backend.domain.member.member.dto.PatchMemberReqDto;
import com.team5.backend.global.entity.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SoftDelete;
import org.hibernate.annotations.SoftDeleteType;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SoftDelete(strategy = SoftDeleteType.DELETED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "memberId")
    private Long memberId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "nickname", nullable = false)
    private String nickname;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "password", nullable = false)
    private String password;

    @CreationTimestamp
    @Column(name = "createdAt", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updatedAt", nullable = false)
    private LocalDateTime updatedAt;

    // 삭제 시간을 기록하는 필드
    @Column(name = "deletedAt")
    private LocalDateTime deletedAt;

    @Embedded
    private Address address;

    @Column(name = "imageUrl")
    private String imageUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Column(name = "emailVerified", nullable = false)
    private Boolean emailVerified;

    public void updateMember(PatchMemberReqDto patchMemberReqDto, PasswordEncoder passwordEncoder) {

        if (patchMemberReqDto.getEmail() != null) this.email = patchMemberReqDto.getEmail();
        if (patchMemberReqDto.getNickname() != null) this.nickname = patchMemberReqDto.getNickname();
        if (patchMemberReqDto.getName() != null) this.name = patchMemberReqDto.getName();
        if (patchMemberReqDto.getPassword() != null) this.password = passwordEncoder.encode(patchMemberReqDto.getPassword());
        if (patchMemberReqDto.getZipCode() != null || patchMemberReqDto.getStreetAdr() != null || patchMemberReqDto.getDetailAdr() != null)
            this.address = patchMemberReqDto.toAddress();
        if (patchMemberReqDto.getImageUrl() != null) this.imageUrl = patchMemberReqDto.getImageUrl();
    }

    public void updatePassword(String rawPassword, PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(rawPassword);
    }

    @PreRemove
    public void onDelete() {
        this.deletedAt = LocalDateTime.now();
    }
}
