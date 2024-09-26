package com.hust.baseweb.applications.education.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ResponseFirstType {

    private final int status;

    private List<Error> errors = new ArrayList<>();

    public ResponseFirstType(int status) {
        this.status = status;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Error {

        private final String location;

        private final String type;

        private final String message;
    }


    public void addError(String location, String type, String message) {
        this.errors.add(new Error(location, type, message));
    }
}
