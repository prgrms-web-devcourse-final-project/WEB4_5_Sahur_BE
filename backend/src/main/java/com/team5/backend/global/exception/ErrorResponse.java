package com.team5.backend.global.exception;

public class ErrorResponse {
    private final boolean success = false;
    private final int status;
    private final String msg; // errorCode.getCode()
    private final String message; // errorCode.getMessage()
    private final Object data = null;

    public ErrorResponse(ErrorCode errorCode) {
        this.status = errorCode.getStatus();
        this.msg = errorCode.getCode();
        this.message = errorCode.getMessage();
    }

    public boolean isSuccess() {
        return success;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public String getMessage() {
        return message;
    }

    public Object getData() {
        return data;
    }
}