package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("Khởi tạo", "CREATED"),
    APPROVED("Đã phê duyệt", "APPROVED"),
    DISTRIBUTED("Đã phân phối", "DISTRIBUTED"),
    DELIVERING_A_PART("Đang giao hàng một phần", "DELIVERING_A_PART"),
    CANCELLED("Đã hủy", "CANCELLED"),
    COMPLETED("Đã hoàn thành giao hàng", "COMPLETED");

    private final String name;
    private final String code;

    public static OrderStatus findByCode(String code) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
