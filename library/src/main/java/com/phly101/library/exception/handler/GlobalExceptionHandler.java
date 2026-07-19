package com.phly101.library.exception.handler;

import com.phly101.library.dto.ErrorResponseRecord;
import com.phly101.library.exception.MainException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MainException.class)
    public ResponseEntity<ErrorResponseRecord> handleMatchException(MainException e) {
        ErrorResponseRecord response = new ErrorResponseRecord(e.getErrorCode(), e.getErrorMessage());
        return ResponseEntity.status(e.getHttpstatus()).body(response);

    }
}
