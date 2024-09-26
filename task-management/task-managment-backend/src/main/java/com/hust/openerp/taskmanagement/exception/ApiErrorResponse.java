package com.hust.openerp.taskmanagement.exception;

import java.util.ArrayList;
import java.util.List;

public class ApiErrorResponse {
    private String code;
    private String message;
    private List<ErrorDetail> errors = new ArrayList<>();

    public ApiErrorResponse(String code, String message, List<ErrorDetail> details) {
        this.code = code;
        this.message = message;
        this.errors = details;
    }

    public ApiErrorResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public ApiErrorResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), errorCode.getMessage());
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public List<ErrorDetail> getErrors() {
        return errors;
    }

    public void setErrors(List<ErrorDetail> details) {
        this.errors = details;
    }

    public static ApiErrorResponse of(String code, String message, List<ErrorDetail> details) {
        return new ApiErrorResponse(code, message, details);
    }

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(code, message);
    }

    public static ApiErrorResponse of(String code, String message, String field, String detail) {
        return new ApiErrorResponse(code, message, List.of(new ErrorDetail(field, detail)));
    }

    public static ApiErrorResponse of(ErrorCode errorCode) {
        return new ApiErrorResponse(errorCode);
    }

    public static class ErrorDetail {
        private String field;
        private String message;

        public ErrorDetail(String field, String message) {
            this.field = field;
            this.message = message;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class Builder {
        private String code;
        private String message;
        private List<ErrorDetail> details = new ArrayList<>();

        public Builder code(String code) {
            this.code = code;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Builder errorCode(ErrorCode errorCode) {
            this.code = errorCode.getCode();
            this.message = errorCode.getMessage();
            return this;
        }

        public Builder errors(List<ErrorDetail> errors) {
            this.details = errors;
            return this;
        }

        public Builder error(String field, String message) {
            this.details.add(new ErrorDetail(field, message));
            return this;
        }

        public ApiErrorResponse build() {
            return new ApiErrorResponse(code, message, details);
        }
    }
}
