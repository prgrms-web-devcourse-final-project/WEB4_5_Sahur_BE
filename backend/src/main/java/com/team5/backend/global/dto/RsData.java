package com.team5.backend.global.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RsData<T> {

    private boolean success;
    private int status;
    private Error error;
    private T data;

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Error {
        private String code;
        private String msg;
    }
}