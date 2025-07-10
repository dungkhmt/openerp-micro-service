package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ReceiptStatus {

    CREATED("Khởi tạo", "CREATED"),
    APPROVED("Đã phê duyệt", "APPROVED"),
    IN_PROGRESS("Đang xử lý", "IN_PROGRESS"),
    CANCELLED("Đã hủy", "CANCELLED"),
    COMPLETED("Đã hoàn thành", "COMPLETED");

    private final String name;
    private final String code;

    public static ReceiptStatus findByCode(String code) {
        for (ReceiptStatus status : ReceiptStatus.values()) {
            if (status.getCode().equals(code)) {
                return status;
            }
        }
        return null;
    }
}
