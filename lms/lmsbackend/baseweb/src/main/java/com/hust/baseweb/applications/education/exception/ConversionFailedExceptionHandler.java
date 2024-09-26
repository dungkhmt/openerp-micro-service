package com.hust.baseweb.applications.education.exception;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ConversionFailedExceptionHandler {

    @ExceptionHandler(ConversionFailedException.class)
    public ResponseEntity<?> handleConversionFailed(ConversionFailedException ex) {
        SimpleResponse response = new SimpleResponse(404, "Conversion failed", ex.getMessage());
        ;

        return ResponseEntity.status(response.getStatus()).body(response);
    }
}
