package com.hust.baseweb.applications.programmingcontest.exception.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ExceptionResponse {
    private int code;
    private String message;
}
