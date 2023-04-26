package com.hust.baseweb.applications.education.classmanagement.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RegistStatus {

    WAITING_FOR_APPROVAL("Chờ phê duyệt"),
    APPROVED("Đã phê duyệt"),
    REFUSED("Đã từ chối"),
    REMOVED("Đã bị xoá");

    private String value;

    public static RegistStatus of(String value) {
        for (RegistStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
