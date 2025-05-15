package com.team5.backend.domain.category.dto;

public record KeywordResDto(
        String code,   // enum 상수명
        String label   // 화면 노출명(displayName)
) { }