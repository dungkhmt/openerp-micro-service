package com.example.shared.exception;

import com.example.shared.response.CommonResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class MyExceptionHandler {

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException e) {
        e.printStackTrace();

        return new ResponseEntity<>(CommonResponse.forbidden(e.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(MyException.class)
    public ResponseEntity<CommonResponse<String>> handleMyException(MyException e) {
        e.printStackTrace();

        return ResponseEntity.status(e.getHttpStatus()).body(e.toMyCommonResponse());
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Object> handleMissingHeaderException(MissingRequestHeaderException e) {
        e.printStackTrace();

        return new ResponseEntity<>(CommonResponse.badRequest(e.getMessage()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        e.printStackTrace();

        return new ResponseEntity<>(CommonResponse.badRequest("Invalid request body"),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleConversionFailedException(IllegalArgumentException e) {
        e.printStackTrace();

        return new ResponseEntity<>(CommonResponse.badRequest(e.getMessage()),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Object> handleValidationException(BindException e) {
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder errorMessage = new StringBuilder();
        e.printStackTrace();
        for (ObjectError objectError : bindingResult.getAllErrors()) {
            if (objectError instanceof FieldError fieldError) {
                errorMessage.append(fieldError.getField())
                    .append(": ")
                    .append(fieldError.getDefaultMessage())
                    .append("; ");
            } else {
                errorMessage.append(objectError.getDefaultMessage())
                    .append("; ");
            }
        }
        return new ResponseEntity<>(CommonResponse.badRequest(errorMessage.toString(), ErrorCodeList.INVALID_PARAMETER),
            HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({InvalidParamsSendException.class, BadCredentialsException.class})
    public ResponseEntity<Object> handleSendResponseException(RuntimeException e) {
        return new ResponseEntity<>(CommonResponse.badRequest(e.getMessage()),
            HttpStatus.BAD_REQUEST);
    }

    

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception e) {
        e.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(CommonResponse.internalError());
    }
}
