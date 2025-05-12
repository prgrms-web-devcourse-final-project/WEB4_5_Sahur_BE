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
    private String msg;     // errorCode.getCode()
    private String message; // errorCode.getMessage() 또는 상세 메시지
    private T data;
}