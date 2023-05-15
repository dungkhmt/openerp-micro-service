package com.hust.wmsbackend.management.model.response;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class ProductGeneralResponse {
    private String productId;
    private String name;
    private String code;
    private BigDecimal retailPrice;
    private BigDecimal onHandQuantity;
    private byte[] imageData;
    private String imageContentType;
}
