package com.team5.backend.global.exception;

import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.code.CommonErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // printStackTrace()는 단순 디버깅용, 비동기 로깅등 성능 최적화 가능한 logger로 변경
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<RsData<Empty>> handleCustomException(CustomException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(RsDataUtil.fail(errorCode));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RsData<Empty>> handleValidationException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors()
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity
                .status(CommonErrorCode.VALIDATION_ERROR.getStatus())
                .body(RsDataUtil.fail(CommonErrorCode.VALIDATION_ERROR, msg));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RsData<Empty>> handleAccessDeniedException(AccessDeniedException ex) {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(RsDataUtil.fail(CommonErrorCode.UNAUTHORIZED));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RsData<Empty>> handleUnexpectedException(Exception ex) {
        log.error("Unhandled exception occurred", ex);
        return ResponseEntity
                .status(CommonErrorCode.INTERNAL_ERROR.getStatus())
                .body(RsDataUtil.fail(CommonErrorCode.INTERNAL_ERROR));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RsData<Empty>> handleTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String msg = String.format("잘못된 값입니다: '%s' 는 허용되지 않는 값입니다.", ex.getValue());
        return ResponseEntity
                .status(CommonErrorCode.VALIDATION_ERROR.getStatus())
                .body(RsDataUtil.fail(CommonErrorCode.VALIDATION_ERROR, msg));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RsData<Empty>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("잘못된 인자 전달: {}", ex.getMessage(), ex);
        return ResponseEntity
                .status(CommonErrorCode.VALIDATION_ERROR.getStatus())
                .body(RsDataUtil.fail(CommonErrorCode.VALIDATION_ERROR, ex.getMessage()));
    }
}