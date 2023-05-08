package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum OrderStatus {

    CREATED("Khởi tạo", "CREATED"),
    APPROVED("Đã phê duyệt", "APPROVED"),
    LAST_DELIVERING("Đang giao hàng", "LAST_DELIVERING"),
    DELIVERING_A_PART("Đang giao hàng một phần", "DELIVERING_A_PART"),
    CANCELLED("Đã hủy", "CANCELLED"),
    CUSTOMER_CANCELLED("Khách hàng hủy", "CUSTOMER_CANCELLED"),
    SUCCESS("Giao hàng thành công", "SUCCESS"),
    FAIL("Giao hàng thất bại", "FAIL"),
    COMPLETED("Đã hoàn thành", "COMPLETED");

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
