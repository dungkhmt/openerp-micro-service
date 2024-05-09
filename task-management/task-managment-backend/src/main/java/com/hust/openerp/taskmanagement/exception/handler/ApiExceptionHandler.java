package com.hust.openerp.taskmanagement.exception.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hust.openerp.taskmanagement.exception.ApiErrorResponse;
import com.hust.openerp.taskmanagement.exception.ApiException;

import lombok.extern.log4j.Log4j2;

@RestControllerAdvice
@Log4j2
public class ApiExceptionHandler {
    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ApiErrorResponse> handleBusinessException(ApiException ex) {
        log.error("ApiException: {} - {}", ex.getCode(), ex.getMessage());
        ApiErrorResponse response = ApiErrorResponse.of(ex.getCode(), ex.getMessage());
        return new ResponseEntity<>(response, ex.getStatus());
    }
}
