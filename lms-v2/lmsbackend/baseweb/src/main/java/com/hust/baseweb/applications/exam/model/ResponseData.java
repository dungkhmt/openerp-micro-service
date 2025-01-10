package com.hust.baseweb.applications.exam.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseData<T> {

    private HttpStatus httpStatus;
    private Integer resultCode;
    private String resultMsg;
    protected T data;
    @Builder.Default
    private OffsetDateTime responseTimestamp =OffsetDateTime.now();

    public ResponseData(HttpStatus httpStatus, Integer resultCode, String resultMsg) {
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.responseTimestamp = OffsetDateTime.now();
    }

    public ResponseData(HttpStatus httpStatus, Integer resultCode, String resultMsg, T data) {
        this.httpStatus = httpStatus;
        this.resultCode = resultCode;
        this.resultMsg = resultMsg;
        this.responseTimestamp = OffsetDateTime.now();
        this.data = data;
    }
}
