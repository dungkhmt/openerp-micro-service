package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssignedOrderItemStatus {

    CREATED("CREATED", "Khởi tạo"),
    WAITING_PICK("WAITING_PICK", "Đợi nhân viên vận chuyển tới lấy hàng"),
    PICK_FAIL("PICK_FAIL", "Lấy hàng thất bại"),
    DELIVERING("DELIVERING", "Đang giao hàng"),
    DELIVERY_FAIL("DELIVERY_FAIL", "Giao hàng thất bại"),
    DONE("DONE", "Giao hàng thành công");

    private final String code;
    private final String name;
}
