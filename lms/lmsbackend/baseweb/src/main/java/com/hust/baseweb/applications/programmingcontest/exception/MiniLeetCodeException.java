package com.hust.baseweb.applications.programmingcontest.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class MiniLeetCodeException extends Exception{
    private int code;

    public MiniLeetCodeException(int code) {
        this.code = code;
    }

    public MiniLeetCodeException(String message, int code) {
        super(message);
        this.code = code;
    }

    public MiniLeetCodeException(String message){
        super(message);
    }

    public MiniLeetCodeException(String message, Throwable cause, int code) {
        super(message, cause);
        this.code = code;
    }

    public MiniLeetCodeException(Throwable cause, int code) {
        super(cause);
        this.code = code;
    }

    public MiniLeetCodeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace, int code) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.code = code;
    }

}
