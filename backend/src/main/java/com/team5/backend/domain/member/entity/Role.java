package com.team5.backend.domain.member.entity;

import lombok.Getter;

@Getter
public enum Role {

    USER("회원"),
    ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }
}
