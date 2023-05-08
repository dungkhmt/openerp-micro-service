package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum DeliveryTripItemStatus {
    CREATED("Khởi tạo", "CREATED"),
    DELIVERING("Đang giao", "DELIVERING"),
    FAIL("Giao thất bại", "FAIL"),
    DONE("Giao thành công", "DONE");

    private final String name;
    private final String code;

    public static DeliveryTripItemStatus findByCode(String code) {
        for (DeliveryTripItemStatus status : DeliveryTripItemStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
