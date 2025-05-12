package com.team5.backend.global.exception;

import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;

public class RsDataUtil {

    public static RsData<Empty> fail(ErrorCode errorCode) {
        return RsData.<Empty>builder()
                .success(false)
                .status(errorCode.getStatus())
                .error(RsData.Error.builder()
                        .code(errorCode.getCode())
                        .msg(errorCode.getMessage())
                        .build())
                .data(null)
                .build();
    }

    public static <T> RsData<T> fail(ErrorCode errorCode, T data) {
        return RsData.<T>builder()
                .success(false)
                .status(errorCode.getStatus())
                .error(RsData.Error.builder()
                        .code(errorCode.getCode())
                        .msg(errorCode.getMessage())
                        .build())
                .data(data)
                .build();
    }

    public static <T> RsData<T> success(String msg, T data) {
        return RsData.<T>builder()
                .success(true)
                .status(200)
                .error(null)
                .data(data)
                .build();
    }

    public static RsData<Empty> success(String msg) {
        return RsData.<Empty>builder()
                .success(true)
                .status(200)
                .error(null)
                .data(null)
                .build();
    }
}
