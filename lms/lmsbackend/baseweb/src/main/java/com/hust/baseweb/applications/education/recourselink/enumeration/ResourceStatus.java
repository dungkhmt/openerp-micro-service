package com.hust.baseweb.applications.education.recourselink.enumeration;

import com.hust.baseweb.applications.education.classmanagement.enumeration.RegistStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResourceStatus {
    RESOURCE_CREATED("RESOURCE_CREATED"),
    RESOURCE_DISABLED("RESOURCE_DISABLED");

    private String value;

    public static ResourceStatus of(String value) {
        for (ResourceStatus status : values()) {
            if (status.getValue().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
