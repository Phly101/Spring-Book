package com.phly101.library.exception.handler;

import com.phly101.library.dto.common.ErrorResponseRecord;
import com.phly101.library.dto.common.ValidationErrorResponseRecord;
import com.phly101.library.exception.MainException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MainException.class)
    public ResponseEntity<ErrorResponseRecord> handleMatchException(MainException e) {
        ErrorResponseRecord response = new ErrorResponseRecord(e.getErrorCode(), e.getErrorMessage());
        return ResponseEntity.status(e.getHttpstatus()).body(response);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseRecord> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> {
            String fieldName = error.getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        ValidationErrorResponseRecord response = new ValidationErrorResponseRecord("VALIDATION_FAILED", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseRecord> handleUnreadableMessage() {
        ErrorResponseRecord response = new ErrorResponseRecord("MALFORMED_REQUEST", "Request body is invalid or contains an unrecognized value");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}
