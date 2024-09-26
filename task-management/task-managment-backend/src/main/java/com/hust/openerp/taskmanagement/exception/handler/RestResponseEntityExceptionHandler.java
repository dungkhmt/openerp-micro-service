package com.hust.openerp.taskmanagement.exception.handler;

import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.hust.openerp.taskmanagement.exception.ApiErrorResponse;
import com.hust.openerp.taskmanagement.exception.ErrorCode;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class RestResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {
    public RestResponseEntityExceptionHandler() {
        super();
    }

    // 400

    /// MethodArgumentNotValidException: This exception is thrown when argument
    /// annotated with @Valid failed validation in request body.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(final MethodArgumentNotValidException ex,
            final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.INVALID_REQUEST_BODY_PARAMETER);
        for (final ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            resBuilder.error(error.getObjectName(), error.getDefaultMessage());
        }
        for (final FieldError error : ex.getBindingResult().getFieldErrors()) {
            resBuilder.error(error.getField(), error.getDefaultMessage());
        }

        var apiError = resBuilder.build();
        return handleExceptionInternal(ex, apiError, headers,
                HttpStatus.BAD_REQUEST,
                request);
    }

    /// TypeMismatchException: This exception is thrown when method argument is not
    /// the
    /// expected type.
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
            HttpStatusCode status, WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.INVALID_REQUEST_PARAMETER_TYPE);
        resBuilder.error(ex.getPropertyName(), ex.getValue() + " should be of type "
                + (ex.getRequiredType() != null ? ex.getRequiredType().getName() : "unknown"));
        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /// MissingServletRequestPartException: This exception is thrown when when the
    /// part
    /// of a multipart request not found
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(final MissingServletRequestPartException ex,
            final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.INVALID_REQUEST_BODY_PARAMETER);
        resBuilder.error(ex.getRequestPartName(), "part is missing");

        var apiError = resBuilder.build();

        return handleExceptionInternal(ex, apiError, headers, ErrorCode.INVALID_REQUEST_BODY_PARAMETER.getStatus(),
                request);
    }

    /// MissingServletRequestParameterException: This exception is thrown when
    /// request
    /// missing parameter
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            final MissingServletRequestParameterException ex, final HttpHeaders headers, final HttpStatusCode status,
            final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.MISSING_REQUEST_PARAMETER);
        resBuilder.error(ex.getParameterName(), "parameter is missing");

        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    /// MethodArgumentTypeMismatchException: This exception is thrown when method
    /// argument is not the expected type
    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })
    public ResponseEntity<ApiErrorResponse> handleMethodArgumentTypeMismatch(
            final MethodArgumentTypeMismatchException ex, final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.INVALID_REQUEST_PARAMETER_TYPE);
        resBuilder.error(ex.getName(), ex.getValue() + " should be of type " + (ex.getRequiredType() != null
                ? ex.getRequiredType().getName()
                : "unknown"));

        var apiError = resBuilder.build();

        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // ConstraintViolationException: This exception reports the result of constraint
    // violations in case of validation error in request parameters.
    @ExceptionHandler({ ConstraintViolationException.class })
    public ResponseEntity<ApiErrorResponse> handleConstraintViolation(final ConstraintViolationException ex,
            final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.INVALID_REQUEST_PARAMETER_VALUE);
        for (final ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            resBuilder.error(violation.getPropertyPath().toString(), violation.getMessage());
        }

        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(),
                HttpStatus.BAD_REQUEST);
    }

    /// HttpMessageNotReadableException: This exception is thrown when request JSON
    /// is
    /// malformed.
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(final HttpMessageNotReadableException ex,
            final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.INVALID_JSON);
        resBuilder.error("request body", "malformed JSON request");

        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }

    // 404

    /// NoHandlerFoundException: This exception is thrown when no handler found for
    /// a request.
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(final NoHandlerFoundException ex,
            final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.NO_HANDLER_FOUND);
        resBuilder.error(ex.getHttpMethod(), "No handler found for " + ex.getHttpMethod() + " " + ex.getRequestURL());

        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.NOT_FOUND);
    }

    // 405

    /// HttpRequestMethodNotSupportedException: This exception is thrown when
    /// request
    /// method is not supported.
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            final HttpRequestMethodNotSupportedException ex, final HttpHeaders headers, final HttpStatusCode status,
            final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.METHOD_NOT_ALLOWED);

        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getMethod());
        builder.append(" method is not supported for this request. Supported methods are ");
        if (ex.getSupportedHttpMethods() != null)
            ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

        resBuilder.error(ex.getMethod(), builder.toString());

        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.METHOD_NOT_ALLOWED);
    }

    // 415

    /// HttpMediaTypeNotSupportedException: This exception is thrown when media type
    /// is
    /// not supported.
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(final HttpMediaTypeNotSupportedException ex,
            final HttpHeaders headers, final HttpStatusCode status, final WebRequest request) {
        logger.info(ex.getClass().getName());
        var resBuilder = new ApiErrorResponse.Builder().errorCode(ErrorCode.UNSUPPORTED_MEDIA_TYPE);

        final StringBuilder builder = new StringBuilder();
        builder.append(ex.getContentType());
        builder.append(" media type is not supported. Supported media types are ");
        ex.getSupportedMediaTypes().forEach(t -> builder.append(t + " "));

        resBuilder.error(ex.getContentType().toString(), builder.toString());

        var apiError = resBuilder.build();
        return new ResponseEntity<>(apiError, new HttpHeaders(), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

    // 401

    /// UnauthorizedException: This exception is thrown when user tries to access a
    /// secured REST resource without providing any credentials.
    @ExceptionHandler({ Unauthorized.class })
    public ResponseEntity<ApiErrorResponse> handleUnauthorized(final Unauthorized ex,
            final WebRequest request) {
        logger.info(ex.getClass().getName());
        var apiError = new ApiErrorResponse.Builder().errorCode(ErrorCode.UNAUTHORIZED)
                .build();
        return new ResponseEntity<>(apiError, ex.getResponseHeaders(), ex.getStatusCode());
    }

    // 500

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleException(Exception e) {
        logger.error("Internal server error", e);
        ApiErrorResponse response = ApiErrorResponse.of(ErrorCode.UNKNOWN_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
