package com.hust.wmsbackend.management.entity.enumentity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum AssignedOrderItemStatus {

    CREATED("CREATED", "Khởi tạo"),
    DONE("DONE", "Giao hàng thành công");

    private final String code;
    private final String name;
}
