package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryTripStatus {
    CREATED("Khởi tạo", "CREATED"),
    DELIVERING("Đang giao", "DELIVERING"),
    DONE("Hoàn thành giao hàng", "DONE");

    private final String name;
    private final String code;

    public static DeliveryTripStatus findByCode(String code) {
        for (DeliveryTripStatus status : DeliveryTripStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
