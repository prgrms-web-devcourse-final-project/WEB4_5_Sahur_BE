package com.team5.backend.global.exception;

public interface ErrorCode {
    int getStatus();
    String getCode();
    String getMessage();
}
