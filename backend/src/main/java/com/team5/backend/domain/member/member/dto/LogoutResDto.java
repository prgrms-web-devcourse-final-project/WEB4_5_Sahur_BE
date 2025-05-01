package com.team5.backend.domain.member.member.dto;

import lombok.Getter;

@Getter
public class LogoutResDto {

    private static final String LOGOUT_SUCCESS_MESSAGE = "로그아웃이 성공적으로 처리되었습니다.";
    private final String message;

    public LogoutResDto() {
        this.message = LOGOUT_SUCCESS_MESSAGE;
    }
}
