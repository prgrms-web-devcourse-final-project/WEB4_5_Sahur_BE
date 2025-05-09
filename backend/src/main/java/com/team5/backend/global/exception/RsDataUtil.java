package com.team5.backend.global.exception;

import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;

public class RsDataUtil {

    public static RsData<Empty> fail(ErrorCode errorCode) {
        return new RsData<>(errorCode.getStatus() + "-1", errorCode.getMessage());
    }

    public static <T> RsData<T> fail(ErrorCode errorCode, T data) {
        return new RsData<>(errorCode.getStatus() + "-1", errorCode.getMessage(), data);
    }

    public static <T> RsData<T> success(String msg, T data) {
        return new RsData<>("200-0", msg, data);
    }

    public static RsData<Empty> success(String msg) {
        return new RsData<>("200-0", msg);
    }
}
