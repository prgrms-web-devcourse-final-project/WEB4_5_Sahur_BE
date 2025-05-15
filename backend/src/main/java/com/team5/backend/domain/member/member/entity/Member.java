package com.team5.backend.domain.member.member.entity;

import com.team5.backend.domain.member.member.dto.PatchMemberReqDto;
import com.team5.backend.global.entity.Address;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE Member SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE member_id = ?")
@FilterDef(name = "deletedMemberFilter", parameters = @ParamDef(name = "isDeleted", type = Boolean.class))
@Filter(name = "deletedMemberFilter", condition = "deleted = :isDeleted")
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

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
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

    public void softDelete() {

        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {

        this.deleted = false;
        this.deletedAt = null;
    }
}
