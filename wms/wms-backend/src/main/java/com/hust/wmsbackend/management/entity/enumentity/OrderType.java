package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum OrderType {
    ONLINE(0, "ONLINE"),
    OFFLINE(0, "OFFLINE");

    private final long code;
    private final String name;

    public static OrderType findByCode(long code) {
        for (OrderType type : OrderType.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        return null;
    }
}
