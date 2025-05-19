package com.team5.backend.global.exception;

import com.team5.backend.global.dto.Empty;
import com.team5.backend.global.dto.RsData;
import com.team5.backend.global.exception.code.CommonErrorCode;
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
        ex.printStackTrace();
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
}