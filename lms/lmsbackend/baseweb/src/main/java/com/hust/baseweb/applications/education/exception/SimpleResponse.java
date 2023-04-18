package com.hust.baseweb.applications.education.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class SimpleResponse {

    private final int status;

    private final String error;

    private String message;

    public void addMessage(String message) {
        this.message += "; " + message;
    }
}
