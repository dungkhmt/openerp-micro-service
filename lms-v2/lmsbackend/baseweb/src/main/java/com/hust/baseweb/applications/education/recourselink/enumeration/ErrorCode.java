package com.hust.baseweb.applications.education.recourselink.enumeration;


import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ErrorCode {
    RESOURCE_EXISTED("RESOURCE_EXISTED"),
    RESOURCE_NOTFOUND("RESOURCE_NOTFOUND"),
    RESOURCE_NAME_EXISTED("RESOURCE_NAME_EXISTED"),
    SUCCEEDED("SUCCEEDED");
    private String value;

    public static ErrorCode of(String value) {
        for (ErrorCode status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
