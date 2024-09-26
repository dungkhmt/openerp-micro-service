package com.example.shared.exception;

public enum ErrorCodeList {
    UNKNOWN_ERROR("TIEP_TD_00", "Unknown Error"),
    INTERNAL_SERVER_ERROR("TIEP_TD_01", "Internal Server Error", true),
    INVALID_PARAMETER("TIEP_TD_02", "Invalid Parameter"),
    ;
    private final String code;
    private final String message;
    private Boolean shouldAlert = false;

    ErrorCodeList(String code, String message) {
        this.code = code;
        this.message = message;
    }

    ErrorCodeList(String code, String message, Boolean shouldAlert) {
        this.code = code;
        this.message = message;
        this.shouldAlert = shouldAlert;
    }

    public String toCode() {
        return this.code;
    }

    @Override
    public String toString() {
        return this.message;
    }

    public Boolean shouldAlert() {
        return shouldAlert;
    }
}
