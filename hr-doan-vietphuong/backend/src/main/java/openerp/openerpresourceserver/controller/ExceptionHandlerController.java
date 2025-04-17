package openerp.openerpresourceserver.controller;

import lombok.extern.slf4j.Slf4j;
import openerp.openerpresourceserver.dto.response.Result;
import openerp.openerpresourceserver.exception.BadRequestException;
import openerp.openerpresourceserver.exception.InvalidRequestException;
import openerp.openerpresourceserver.exception.NotFoundException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Objects;

@ControllerAdvice
@Slf4j
public class ExceptionHandlerController {

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Result> authException(AuthenticationException ex) {
        log.error("Authentication exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(ex.getMessage()));
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<Result> jwtException(JwtException ex) {
        log.error("JWT exception: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Result.error(ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Result> httpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Result.error(ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Result> methodArgumentNotValid(MethodArgumentNotValidException ex) {
        return ResponseEntity.badRequest().body(Result.error(Objects.requireNonNull(ex.getFieldError()).getDefaultMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        String message = "The required type of " +
                ex.getPropertyName() +
                " is " +
                Objects.requireNonNull(ex.getRequiredType()).getSimpleName();
        return ResponseEntity.badRequest().body(Result.error(message));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Result> missingServletRequestParameterException(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body(Result.error(ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Result> constraintViolationException(ConstraintViolationException ex) {
        return ResponseEntity.badRequest().body(Result.error(ex.getMessage()));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Result> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Result.error(ex.getMessage()));
    }
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<Result> badRequestException(BadRequestException ex) {
        return ResponseEntity.badRequest().body(Result.error(ex.getMessage()));
    }
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Result> badRequestException(InvalidRequestException ex) {
        return ResponseEntity.badRequest().body(Result.error(ex.getMessage()));
    }
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> unknownException(Exception ex) {
        log.error("Unknown exception: {}", ex.getMessage(), ex);
        return ResponseEntity.internalServerError().body(Result.error(ex.getMessage()));
    }
}
