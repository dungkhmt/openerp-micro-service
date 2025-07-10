package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@ToString
public class ProductDiffHistory {
    private String productId;
    private String productName;
    private BigDecimal quantity;
    private String effectiveDateStr;
    private String type;

    public ProductDiffHistory(String productId, String productName, BigDecimal quantity, String effectiveDateStr, String type) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.effectiveDateStr = effectiveDateStr;
        ProductHistoryType productHistoryType = ProductHistoryType.findByCode(type);
        this.type = productHistoryType != null ? productHistoryType.getName() : null;
    }

    @AllArgsConstructor
    @Getter
    public enum ProductHistoryType {
        EXPORT("Xuất hàng", "EXPORT"),
        IMPORT("Nhập hàng", "IMPORT"),
        ASSIGNED("Đã phân phối", "ASSIGNED"),
        WAIT_DELIVERY("Chờ giao hàng", "WAIT_DELIVERY"),
        DELIVERING("Đang giao hàng", "DELIVERING");

        private final String name;
        private final String code;

        public static ProductHistoryType findByCode(String code) {
            for (ProductHistoryType type : ProductHistoryType.values()) {
                if (type.getCode().equals(code)) {
                    return type;
                }
            }
            return null;
        }
    }
}
